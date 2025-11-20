package cn.augrain.easy.tool.time;

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
     * 支持 LocalDate, LocalDateTime
     */
    public static <T extends Temporal & Comparable<? super T>> T max(T date1, T date2) {
        if (date1 == null && date2 == null) return null;
        if (date1 == null) return date2;
        if (date2 == null) return date1;
        return date1.compareTo((T) date2) >= 0 ? date1 : date2;
    }

    /**
     * 比较两个 Temporal 对象并返回最小值
     * 支持 LocalDate, LocalDateTime
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
}
