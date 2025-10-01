package cn.augrain.easy.tool.algorithm.timewheel;

import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

/**
 * 重复任务实现，继承自TimeWheelTask
 *
 * @author biaoy
 * @since 2025/10/01
 */
public class RepeatingTimeWheelTask extends TimeWheelTask {
    @Getter
    private final int intervalSeconds;
    @Getter
    private final int maxExecutions;
    private final Predicate<RepeatingTimeWheelTask> stopCondition;

    private final AtomicInteger executionCount;
    private final AtomicLong totalExecutionTimeMs;
    private final AtomicBoolean shouldStop;
    private final AtomicBoolean completed;

    private volatile long nextExecutionTimeMs;
    private volatile boolean isRepeating;

    public RepeatingTimeWheelTask(String taskId, Runnable task, int intervalSeconds) {
        this(taskId, task, intervalSeconds, -1, null);
    }

    public RepeatingTimeWheelTask(String taskId, Runnable task, int intervalSeconds,
                                  int maxExecutions) {
        this(taskId, task, intervalSeconds, maxExecutions, null);
    }

    public RepeatingTimeWheelTask(String taskId, Runnable task, int intervalSeconds,
                                  Predicate<RepeatingTimeWheelTask> stopCondition) {
        this(taskId, task, intervalSeconds, -1, stopCondition);
    }

    public RepeatingTimeWheelTask(String taskId, Runnable task, int intervalSeconds,
                                  int maxExecutions, Predicate<RepeatingTimeWheelTask> stopCondition) {
        super(taskId, task, intervalSeconds);

        this.intervalSeconds = intervalSeconds;
        this.maxExecutions = maxExecutions;
        this.stopCondition = stopCondition;

        this.executionCount = new AtomicInteger(0);
        this.totalExecutionTimeMs = new AtomicLong(0);
        this.shouldStop = new AtomicBoolean(false);
        this.completed = new AtomicBoolean(false);

        this.isRepeating = true;
        this.nextExecutionTimeMs = System.currentTimeMillis() + intervalSeconds * 1000L;
    }

    @Override
    public void execute() {
        if (isCancelled() || completed.get()) {
            return;
        }

        long startTime = System.currentTimeMillis();
        try {
            super.execute();
        } finally {
            long executionTime = System.currentTimeMillis() - startTime;
            totalExecutionTimeMs.addAndGet(executionTime);

            int currentCount = executionCount.incrementAndGet();

            if (shouldStopAfterExecution(currentCount)) {
                completed.set(true);
                isRepeating = false;
            } else {
                scheduleNextExecution();
            }
        }
    }

    private boolean shouldStopAfterExecution(int currentCount) {
        if (shouldStop.get()) {
            return true;
        }

        if (maxExecutions > 0 && currentCount >= maxExecutions) {
            return true;
        }

        if (stopCondition != null) {
            try {
                return stopCondition.test(this);
            } catch (Exception e) {
                System.err.println("Error in stop condition for task " + getTaskId() + ": " + e.getMessage());
                return true;
            }
        }

        return false;
    }

    private void scheduleNextExecution() {
        nextExecutionTimeMs = System.currentTimeMillis() + intervalSeconds * 1000L;
    }

    public void stop() {
        shouldStop.set(true);
        isRepeating = false;
    }

    public boolean isRepeating() {
        return isRepeating && !completed.get() && !isCancelled();
    }

    public boolean isCompleted() {
        return completed.get();
    }

    public int getExecutionCount() {
        return executionCount.get();
    }

    public long getTotalExecutionTimeMs() {
        return totalExecutionTimeMs.get();
    }

    public double getAverageExecutionTimeMs() {
        int count = executionCount.get();
        return count > 0 ? (double) totalExecutionTimeMs.get() / count : 0.0;
    }

    public long getNextExecutionTimeMs() {
        return nextExecutionTimeMs;
    }

    public long getTimeToNextExecutionMs() {
        return Math.max(0, nextExecutionTimeMs - System.currentTimeMillis());
    }

    public boolean hasStopCondition() {
        return stopCondition != null;
    }

    @Override
    public String toString() {
        return String.format("RepeatingTimeWheelTask{id='%s', interval=%ds, executions=%d/%d, " +
                        "avgTime=%.2fms, nextIn=%ds, repeating=%s, completed=%s}",
                getTaskId(), intervalSeconds, executionCount.get(),
                maxExecutions > 0 ? maxExecutions : Integer.MAX_VALUE,
                getAverageExecutionTimeMs(),
                getTimeToNextExecutionMs() / 1000,
                isRepeating(), isCompleted());
    }
}