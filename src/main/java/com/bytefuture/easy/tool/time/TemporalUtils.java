package com.bytefuture.easy.tool.time;

import java.time.temporal.Temporal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

/**
 * TemporalUtils
 *
 * @author biaoy
 * @since 2025/11/20
 */
public class TemporalUtils {

    /**
     * 比较两个 Temporal 对象并返回最大值
     */
    public static <T extends Temporal & Comparable<? super T>> T max(T date1, T date2) {
        if (date1 == null && date2 == null) return null;
        if (date1 == null) return date2;
        if (date2 == null) return date1;
        return date1.compareTo((T) date2) >= 0 ? date1 : date2;
    }

    /**
     * 比较两个 Temporal 对象并返回最小值
     */
    public static <T extends Temporal & Comparable<? super T>> T min(T date1, T date2) {
        if (date1 == null && date2 == null) return null;
        if (date1 == null) return date2;
        if (date2 == null) return date1;
        return date1.compareTo((T) date2) <= 0 ? date1 : date2;
    }

    /**
     * 返回多个 Temporal 对象中的最大值
     */
    public static <T extends Temporal & Comparable<? super T>> T max(T... dates) {
        if (dates == null || dates.length == 0) return null;
        return Arrays.stream(dates)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 返回多个 Temporal 对象中的最小值
     */
    public static <T extends Temporal & Comparable<? super T>> T min(T... dates) {
        if (dates == null || dates.length == 0) return null;
        return Arrays.stream(dates)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 使用列表返回最大值
     */
    public static <T extends Temporal & Comparable<? super T>> T max(List<T> dates) {
        if (dates == null || dates.isEmpty()) return null;
        return dates.stream()
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 使用列表返回最小值
     */
    public static <T extends Temporal & Comparable<? super T>> T min(List<T> dates) {
        if (dates == null || dates.isEmpty()) return null;
        return dates.stream()
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);
    }

    /**
     * 是否在区间内
     *
     * @param date  指定日期、时间、日期时间
     * @param start 开始边界
     * @param end   结束边界
     */
    public static <T extends Temporal & Comparable<? super T>> boolean isBetween(T date, T start, T end) {
        return start.compareTo(date) <= 0 && end.compareTo(date) >= 0;
    }

    /**
     * 是否在区间内
     *
     * @param date           指定日期、时间、日期时间
     * @param start          开始边界
     * @param end            结束边界
     * @param startInclusive 是否包含开始边界
     * @param endInclusive   是否包含结束边界
     */
    public static <T extends Temporal & Comparable<? super T>> boolean isBetween(T date, T start, T end,
                                                                                 boolean startInclusive, boolean endInclusive) {
        boolean afterStart = true;
        boolean beforeEnd = true;

        if (start != null) {
            afterStart = startInclusive ? start.compareTo(date) <= 0 : start.compareTo(date) < 0;
        }

        if (end != null) {
            beforeEnd = endInclusive ? end.compareTo(date) >= 0 : end.compareTo(date) > 0;
        }

        return afterStart && beforeEnd;
    }
}
