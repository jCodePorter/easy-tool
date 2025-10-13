package cn.augrain.easy.tool.algorithm.timewheel;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;

/**
 * 自适应时间轮管理器
 * 动态创建和管理时间轮层级
 * 读写锁保证线程安全
 *
 * @author biaoy
 * @since 2025/10/01
 */
@Slf4j
public class AdaptiveTimeWheel {
    private static final int BASE_SLOT_SIZE = 10;
    private static final int BASE_TICK_MS = 1000;
    private static final int MAX_LEVELS = 10;

    private final TimeWheel baseWheel;
    private volatile TimeWheel topLevelWheel;
    private final ReentrantReadWriteLock wheelLock;

    private final ScheduledExecutorService scheduler;
    private final ExecutorService taskExecutor;
    private final AtomicBoolean running;
    private final AtomicLong taskCounter;
    private final ConcurrentHashMap<String, RepeatingTimeWheelTask> repeatingTasks;

    public AdaptiveTimeWheel() {
        this.wheelLock = new ReentrantReadWriteLock();
        this.baseWheel = new TimeWheel(BASE_SLOT_SIZE, BASE_TICK_MS, 0);
        this.topLevelWheel = baseWheel;

        // 调度线程
        this.scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "AdaptiveTimeWheel-Ticker");
            t.setDaemon(true);
            return t;
        });

        // 任务执行线程
        this.taskExecutor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors(), r -> {
            Thread t = new Thread(r, "AdaptiveTimeWheel-Task-Executor");
            t.setDaemon(true);
            return t;
        });

        this.running = new AtomicBoolean(true);
        this.taskCounter = new AtomicLong(0);
        this.repeatingTasks = new ConcurrentHashMap<>();

        startTicker();
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

        String taskId = "repeating-" + taskCounter.incrementAndGet();
        RepeatingTimeWheelTask repeatingTask = new RepeatingTimeWheelTask(
                taskId, task, intervalSeconds, maxExecutions, stopCondition);

        repeatingTasks.put(taskId, repeatingTask);
        scheduleNextExecution(repeatingTask);
        return taskId;
    }

    private void scheduleNextExecution(RepeatingTimeWheelTask task) {
        if (!task.isRepeating()) {
            return;
        }

        try {
            addTask(task);
        } catch (Exception e) {
            log.error("Error scheduling repeating task " + task.getTaskId() + ": ", e);
            task.stop();
            repeatingTasks.remove(task.getTaskId());
        }
    }

    private void addTask(RepeatingTimeWheelTask task) {
        placeTaskInAppropriateWheel(task);
    }

    private void startTicker() {
        scheduler.scheduleAtFixedRate(() -> {
            if (!running.get()) {
                return;
            }
            try {
                tick();
            } catch (Exception e) {
                log.error("Error during time wheel tick: ", e);
            }
        }, BASE_TICK_MS, BASE_TICK_MS, TimeUnit.MILLISECONDS);
    }

    private void tick() {
        wheelLock.readLock().lock();
        try {
            TimeWheel current = baseWheel;

            // 驱动时间轮向前推动一秒
            current.advance();
            executeTasksFromWheel(current);
        } finally {
            wheelLock.readLock().unlock();
        }
    }

    private void executeTasksFromWheel(TimeWheel wheel) {
        if (wheel.getLevel() == 0) {
            ConcurrentLinkedQueue<TimeWheelTask> tasks = wheel.getTasksFromCurrentSlot();
            executeTasks(tasks);
        }
    }

    /**
     * 将任务添加到合适的轮子上
     *
     * @param task 待调度的任务
     */
    private void placeTaskInAppropriateWheel(TimeWheelTask task) {
        wheelLock.readLock().lock();
        try {
            TimeWheel current = baseWheel;
            while (current != null) {
                if (current.canHandleDelay(task.getDelaySeconds())) {
                    current.addTask(task);
                    return;
                }
                current = current.getParent();
            }
        } finally {
            wheelLock.readLock().unlock();
        }

        ensureCapacityForTask(task);
    }

    private void ensureCapacityForTask(TimeWheelTask task) {
        wheelLock.writeLock().lock();
        try {
            TimeWheel current = topLevelWheel;
            int taskDelay = task.getDelaySeconds();

            while (!current.canHandleDelay(taskDelay)) {
                if (current.getLevel() >= MAX_LEVELS - 1) {
                    throw new IllegalArgumentException("Task delay too large: " + taskDelay + " seconds");
                }

                TimeWheel newWheel = createHigherLevelWheel(current);
                current.setParent(newWheel);
                newWheel.setChild(current);
                topLevelWheel = newWheel;
                current = newWheel;
            }

            placeTaskInAppropriateWheel(task);
        } finally {
            wheelLock.writeLock().unlock();
        }
    }

    private TimeWheel createHigherLevelWheel(TimeWheel current) {
        int newLevel = current.getLevel() + 1;
        int newSlotSize = BASE_SLOT_SIZE;
        int newTickMs = current.getTickMs() * current.getSlotSize();

        return new TimeWheel(newSlotSize, newTickMs, newLevel);
    }

    public String addTask(Runnable task, int delaySeconds) {
        if (!running.get()) {
            throw new IllegalStateException("AdaptiveTimeWheel is shutdown");
        }

        if (delaySeconds <= 0) {
            throw new IllegalArgumentException("Delay must be positive");
        }

        String taskId = "task-" + taskCounter.incrementAndGet();
        TimeWheelTask timeWheelTask = new TimeWheelTask(taskId, task, delaySeconds);

        placeTaskInAppropriateWheel(timeWheelTask);
        return taskId;
    }

    private void executeTasks(ConcurrentLinkedQueue<TimeWheelTask> tasks) {
        TimeWheelTask task;
        while ((task = tasks.poll()) != null) {
            if (!task.isCancelled()) {
                final TimeWheelTask executeTask = task;
                taskExecutor.submit(() -> {
                    try {
                        executeTask.execute();
                    } catch (Exception e) {
                        log.error("Error executing task {} , error msg is {}", executeTask.getTaskId(), e.getMessage());
                    }
                });
                if (task instanceof RepeatingTimeWheelTask) {
                    scheduleNextExecution((RepeatingTimeWheelTask) task);
                }
            }
        }
    }

    public int getTotalPendingTasks() {
        wheelLock.readLock().lock();
        try {
            int total = 0;
            TimeWheel current = baseWheel;
            while (current != null) {
                total += current.getPendingTaskCount();
                current = current.getParent();
            }
            return total;
        } finally {
            wheelLock.readLock().unlock();
        }
    }

    public int getWheelLevels() {
        wheelLock.readLock().lock();
        try {
            return topLevelWheel.getLevel() + 1;
        } finally {
            wheelLock.readLock().unlock();
        }
    }

    public void shutdown() {
        running.set(false);

        scheduler.shutdown();
        taskExecutor.shutdown();

        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
            if (!taskExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                taskExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            taskExecutor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        wheelLock.writeLock().lock();
        try {
            TimeWheel current = baseWheel;
            while (current != null) {
                current.shutdown();
                current = current.getParent();
            }
        } finally {
            wheelLock.writeLock().unlock();
        }
    }

    public boolean isRunning() {
        return running.get();
    }

    @Override
    public String toString() {
        return String.format("AdaptiveTimeWheel{running=%s, levels=%d, pendingTasks=%d}",
                running.get(), getWheelLevels(), getTotalPendingTasks());
    }
}