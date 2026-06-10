package com.bytefuture.easy.tool.algorithm.timewheel;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 时间轮实现，支持任意层级
 * 父子关系管理，形成时间轮链
 * 智能任务分配和级联
 *
 * @author biaoy
 * @since 2025/10/01
 */
public class TimeWheel {
    @Getter
    private final int slotSize;

    @Getter
    private final int tickMs;

    @Getter
    private final int level;

    private final AtomicInteger currentSlot;

    private final ConcurrentLinkedQueue<TimeWheelTask>[] slots;

    @Getter
    @Setter
    private TimeWheel parent;

    @Getter
    @Setter
    private TimeWheel child;

    private volatile boolean running;

    @SuppressWarnings("unchecked")
    public TimeWheel(int slotSize, int tickMs, int level) {
        this.slotSize = slotSize;
        this.tickMs = tickMs;
        this.level = level;
        this.currentSlot = new AtomicInteger(0);
        this.slots = new ConcurrentLinkedQueue[slotSize];
        this.running = true;

        for (int i = 0; i < slotSize; i++) {
            slots[i] = new ConcurrentLinkedQueue<>();
        }
    }

    /**
     * 重新分派任务
     */
    public void redistributeTask(TimeWheelTask task, int remainingDelay) {
        if (!running) {
            return;
        }
        int slotIndex = calculateSlotIndex(remainingDelay);
        slots[slotIndex].offer(task);
    }

    public void addTask(TimeWheelTask task) {
        if (!running) {
            return;
        }

        int delaySeconds = task.getDelaySeconds();
        int slotIndex = calculateSlotIndex(delaySeconds);
        if (slotIndex >= 0 && slotIndex < slotSize) {
            slots[slotIndex].offer(task);
            return;
        }

        if (parent != null) {
            parent.addTask(task);
        }
    }

    private int calculateSlotIndex(int delaySeconds) {
        int delayMs = delaySeconds * 1000;
        int ticks = delayMs / tickMs;
        return (currentSlot.get() + ticks) % slotSize;
    }

    public ConcurrentLinkedQueue<TimeWheelTask> getTasksFromCurrentSlot() {
        int slot = currentSlot.get();
        ConcurrentLinkedQueue<TimeWheelTask> tasks = slots[slot];
        ConcurrentLinkedQueue<TimeWheelTask> result = new ConcurrentLinkedQueue<>();

        TimeWheelTask task;
        while ((task = tasks.poll()) != null) {
            result.offer(task);
        }
        return result;
    }

    public void advance() {
        if (!running) {
            return;
        }

        int nextSlot = (currentSlot.get() + 1) % slotSize;
        currentSlot.set(nextSlot);

        if (nextSlot == 0 && parent != null) {
            parent.advance();
            cascadeTasksFromParent();
        }
    }

    private void cascadeTasksFromParent() {
        if (parent != null) {
            ConcurrentLinkedQueue<TimeWheelTask> parentTasks = parent.getTasksFromCurrentSlot();
            for (TimeWheelTask task : parentTasks) {
                if (!task.isCancelled()) {
                    int remainingDelay = calculateRemainingDelay(task);
                    if (remainingDelay < tickMs / 1000 && child != null) {
                        child.redistributeTask(task, remainingDelay);
                    } else {
                        redistributeTask(task, remainingDelay);
                    }
                }
            }
        }
    }

    private int calculateRemainingDelay(TimeWheelTask task) {
        long currentTime = System.currentTimeMillis();
        long executeTime = task.getExecuteTimeMs();
        return (int) Math.max(0, (executeTime - currentTime) / 1000);
    }

    public boolean canHandleDelay(int delaySeconds) {
        int maxDelay = slotSize * tickMs / 1000;
        return delaySeconds <= maxDelay;
    }

    public int getPendingTaskCount() {
        int count = 0;
        for (int i = 0; i < slotSize; i++) {
            count += slots[i].size();
        }
        return count;
    }

    public void shutdown() {
        running = false;
        for (int i = 0; i < slotSize; i++) {
            slots[i].clear();
        }
    }

    @Override
    public String toString() {
        return String.format("DynamicTimeWheel{level=%d, currentSlot=%d, slotSize=%d, tickMs=%d, pendingTasks=%d}",
                level, currentSlot.get(), slotSize, tickMs, getPendingTaskCount());
    }
}