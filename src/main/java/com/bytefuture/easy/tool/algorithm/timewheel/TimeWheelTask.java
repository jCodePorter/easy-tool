package com.bytefuture.easy.tool.algorithm.timewheel;

import lombok.Getter;

/**
 * 基础任务类
 *
 * @author biaoy
 * @since 2025/10/01
 */
public class TimeWheelTask implements Comparable<TimeWheelTask> {
    private final Runnable task;

    @Getter
    private final String taskId;

    @Getter
    private final long executeTimeMs;

    @Getter
    private final int delaySeconds;

    @Getter
    private volatile boolean cancelled;

    public TimeWheelTask(String taskId, Runnable task, int delaySeconds) {
        this.taskId = taskId;
        this.task = task;
        this.delaySeconds = delaySeconds;
        this.executeTimeMs = System.currentTimeMillis() + delaySeconds * 1000L;
        this.cancelled = false;
    }

    public void execute() {
        if (!cancelled && task != null) {
            task.run();
        }
    }

    public void cancel() {
        cancelled = true;
    }

    @Override
    public int compareTo(TimeWheelTask other) {
        return Long.compare(this.executeTimeMs, other.executeTimeMs);
    }

    @Override
    public String toString() {
        return String.format("TimeWheelTask{id='%s', delay=%ds, executeTime=%d}",
                taskId, delaySeconds, executeTimeMs);
    }
}