package cn.augrain.easy.tool.algorithm.timewheel;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * 增强版自适应时间轮，内置重复任务支持
 *
 * @author biaoy
 * @since 2025/10/01
 */
@Slf4j
public class EnhancedAdaptiveTimeWheel extends AdaptiveTimeWheel {
    private final ScheduledExecutorService repeatingTaskMonitor;
    private final ConcurrentHashMap<String, RepeatingTimeWheelTask> repeatingTasks;
    private final AtomicLong repeatingTaskCounter;

    public EnhancedAdaptiveTimeWheel() {
        super();
        this.repeatingTaskMonitor = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "EnhancedTimeWheel-RepeatingMonitor");
            t.setDaemon(true);
            return t;
        });
        this.repeatingTasks = new ConcurrentHashMap<>();
        this.repeatingTaskCounter = new AtomicLong(0);

        startRepeatingTaskMonitor();
    }

    public String addRepeatingTask(Runnable task, int intervalSeconds) {
        return addRepeatingTask(task, intervalSeconds, -1, null);
    }

    public String addRepeatingTask(Runnable task, int intervalSeconds, int maxExecutions) {
        return addRepeatingTask(task, intervalSeconds, maxExecutions, null);
    }

    public String addRepeatingTask(Runnable task, int intervalSeconds,
                                   Predicate<RepeatingTimeWheelTask> stopCondition) {
        return addRepeatingTask(task, intervalSeconds, -1, stopCondition);
    }

    public String addRepeatingTask(Runnable task, int intervalSeconds, int maxExecutions,
                                   Predicate<RepeatingTimeWheelTask> stopCondition) {
        if (!isRunning()) {
            throw new IllegalStateException("EnhancedAdaptiveTimeWheel is shutdown");
        }

        if (intervalSeconds <= 0) {
            throw new IllegalArgumentException("Interval must be positive");
        }

        String taskId = "repeating-" + repeatingTaskCounter.incrementAndGet();
        RepeatingTimeWheelTask repeatingTask = new RepeatingTimeWheelTask(
                taskId, task, intervalSeconds, maxExecutions, stopCondition);

        repeatingTasks.put(taskId, repeatingTask);
        scheduleNextExecution(repeatingTask);
        return taskId;
    }

    private void startRepeatingTaskMonitor() {
        repeatingTaskMonitor.scheduleAtFixedRate(() -> {
            try {
                manageRepeatingTasks();
            } catch (Exception e) {
                log.error("Error in repeating task monitor ", e);
            }
        }, 1, 1, TimeUnit.SECONDS);
    }

    private void manageRepeatingTasks() {
        repeatingTasks.entrySet().removeIf(entry -> {
            RepeatingTimeWheelTask task = entry.getValue();
            return task.isCompleted() || task.isCancelled();
        });
    }

    private void scheduleNextExecution(RepeatingTimeWheelTask task) {
        if (!task.isRepeating()) {
            return;
        }

        int delaySeconds = task.getIntervalSeconds();
        try {
            super.addTask(() -> {
                if (task.isRepeating()) {
                    task.execute();
                    scheduleNextExecution(task);
                }
            }, delaySeconds);
        } catch (Exception e) {
            log.error("Error scheduling repeating task " + task.getTaskId() + ": ", e);
            task.stop();
        }
    }

    public boolean stopRepeatingTask(String taskId) {
        RepeatingTimeWheelTask task = repeatingTasks.get(taskId);
        if (task != null) {
            task.stop();
            return true;
        }
        return false;
    }

    public int getRepeatingTaskCount() {
        return repeatingTasks.size();
    }

    public int getActiveRepeatingTaskCount() {
        return (int) repeatingTasks.values().stream()
                .filter(RepeatingTimeWheelTask::isRepeating)
                .count();
    }

    public int getCompletedRepeatingTaskCount() {
        return (int) repeatingTasks.values().stream()
                .filter(RepeatingTimeWheelTask::isCompleted)
                .count();
    }

    @Override
    public void shutdown() {
        repeatingTaskMonitor.shutdown();

        repeatingTasks.values().forEach(task -> {
            task.stop();
            task.cancel();
        });
        repeatingTasks.clear();

        super.shutdown();
        try {
            if (!repeatingTaskMonitor.awaitTermination(5, TimeUnit.SECONDS)) {
                repeatingTaskMonitor.shutdownNow();
            }
        } catch (InterruptedException e) {
            repeatingTaskMonitor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public int getTotalPendingTasks() {
        return super.getTotalPendingTasks() + getActiveRepeatingTaskCount();
    }

    @Override
    public String toString() {
        return String.format("EnhancedAdaptiveTimeWheel{running=%s, levels=%d, " +
                        "regularTasks=%d, repeatingTasks=%d, activeRepeating=%d}",
                isRunning(), getWheelLevels(),
                super.getTotalPendingTasks(), getRepeatingTaskCount(),
                getActiveRepeatingTaskCount());
    }
}