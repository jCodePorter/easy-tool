package com.bytefuture.easy.tool.algorithm.timewheel;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
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
                log.error("Error in stop condition for task {}: {}", getTaskId(), e.getMessage());
                return true;
            }
        }
        return false;
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

    @Override
    public String toString() {
        return String.format("RepeatingTimeWheelTask{id='%s', interval=%ds, executions=%d/%d, " +
                        "avgTime=%.2fms, totalExecTime=%dms, repeating=%s, completed=%s}",
                getTaskId(), intervalSeconds, executionCount.get(),
                maxExecutions > 0 ? maxExecutions : Integer.MAX_VALUE,
                getAverageExecutionTimeMs(),
                getTotalExecutionTimeMs(),
                isRepeating(), isCompleted());
    }
}