package cn.augrain.easy.tool.algorithm.timewheel;

import java.util.function.Predicate;

/**
 * 条件终止策略集合
 *
 * @author biaoy
 * @since 2025/10/01
 */
public class TaskStopConditions {

    public static class TimeBasedStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final long stopTimeMs;

        public TimeBasedStopCondition(long stopTimeMs) {
            this.stopTimeMs = stopTimeMs;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            return System.currentTimeMillis() >= stopTimeMs;
        }

        public static TimeBasedStopCondition afterSeconds(int seconds) {
            return new TimeBasedStopCondition(System.currentTimeMillis() + seconds * 1000L);
        }

        public static TimeBasedStopCondition afterMinutes(int minutes) {
            return afterSeconds(minutes * 60);
        }

        public static TimeBasedStopCondition afterHours(int hours) {
            return afterMinutes(hours * 60);
        }

        @Override
        public String toString() {
            long remainingMs = stopTimeMs - System.currentTimeMillis();
            return String.format("TimeBasedStopCondition{remainingTime=%ds}", Math.max(0, remainingMs / 1000));
        }
    }

    public static class ExecutionCountStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final int targetCount;

        public ExecutionCountStopCondition(int targetCount) {
            this.targetCount = targetCount;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            return task.getExecutionCount() >= targetCount;
        }

        @Override
        public String toString() {
            return String.format("ExecutionCountStopCondition{targetCount=%d}", targetCount);
        }
    }

    public static class RunningTimeStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final long maxRunningTimeMs;
        private final long startTimeMs;

        public RunningTimeStopCondition(long maxRunningTimeMs) {
            this.maxRunningTimeMs = maxRunningTimeMs;
            this.startTimeMs = System.currentTimeMillis();
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            return (System.currentTimeMillis() - startTimeMs) >= maxRunningTimeMs;
        }

        public static RunningTimeStopCondition maxRunningTimeSeconds(int seconds) {
            return new RunningTimeStopCondition(seconds * 1000L);
        }

        public static RunningTimeStopCondition maxRunningTimeMinutes(int minutes) {
            return maxRunningTimeSeconds(minutes * 60);
        }

        @Override
        public String toString() {
            long elapsedMs = System.currentTimeMillis() - startTimeMs;
            return String.format("RunningTimeStopCondition{elapsed=%ds, max=%ds}",
                    elapsedMs / 1000, maxRunningTimeMs / 1000);
        }
    }

    public static class PerformanceStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final double maxAverageTimeMs;
        private final int minExecutions;

        public PerformanceStopCondition(double maxAverageTimeMs, int minExecutions) {
            this.maxAverageTimeMs = maxAverageTimeMs;
            this.minExecutions = minExecutions;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            if (task.getExecutionCount() < minExecutions) {
                return false;
            }
            return task.getAverageExecutionTimeMs() > maxAverageTimeMs;
        }

        @Override
        public String toString() {
            return String.format("PerformanceStopCondition{maxAvgTime=%.2fms, minExecutions=%d}",
                    maxAverageTimeMs, minExecutions);
        }
    }

    public static class CustomCondition implements Predicate<RepeatingTimeWheelTask> {
        private final Predicate<RepeatingTimeWheelTask> condition;
        private final String description;

        public CustomCondition(Predicate<RepeatingTimeWheelTask> condition, String description) {
            this.condition = condition;
            this.description = description;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            return condition.test(task);
        }

        @Override
        public String toString() {
            return "CustomCondition{" + description + "}";
        }
    }

    public static class CompositeStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final Predicate<RepeatingTimeWheelTask>[] conditions;
        private final boolean requireAll;

        @SafeVarargs
        public CompositeStopCondition(boolean requireAll, Predicate<RepeatingTimeWheelTask>... conditions) {
            this.conditions = conditions;
            this.requireAll = requireAll;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            if (requireAll) {
                for (Predicate<RepeatingTimeWheelTask> condition : conditions) {
                    if (!condition.test(task)) {
                        return false;
                    }
                }
                return true;
            } else {
                for (Predicate<RepeatingTimeWheelTask> condition : conditions) {
                    if (condition.test(task)) {
                        return true;
                    }
                }
                return false;
            }
        }

        @Override
        public String toString() {
            String operator = requireAll ? "AND" : "OR";
            StringBuilder sb = new StringBuilder("CompositeStopCondition{");
            for (int i = 0; i < conditions.length; i++) {
                if (i > 0) sb.append(" ").append(operator).append(" ");
                sb.append(conditions[i].toString());
            }
            sb.append("}");
            return sb.toString();
        }
    }

    public static CompositeStopCondition all(Predicate<RepeatingTimeWheelTask>... conditions) {
        return new CompositeStopCondition(true, conditions);
    }

    public static CompositeStopCondition any(Predicate<RepeatingTimeWheelTask>... conditions) {
        return new CompositeStopCondition(false, conditions);
    }

    public static class ConditionalStopCondition implements Predicate<RepeatingTimeWheelTask> {
        private final java.util.concurrent.atomic.AtomicInteger counter;
        private final int targetCount;
        private final Predicate<RepeatingTimeWheelTask> condition;

        public ConditionalStopCondition(int targetCount, Predicate<RepeatingTimeWheelTask> condition) {
            this.counter = new java.util.concurrent.atomic.AtomicInteger(0);
            this.targetCount = targetCount;
            this.condition = condition;
        }

        @Override
        public boolean test(RepeatingTimeWheelTask task) {
            if (condition.test(task)) {
                int currentCount = counter.incrementAndGet();
                return currentCount >= targetCount;
            }
            return false;
        }

        @Override
        public String toString() {
            return String.format("ConditionalStopCondition{condition=%s, count=%d/%d}",
                    condition.toString(), counter.get(), targetCount);
        }
    }
}