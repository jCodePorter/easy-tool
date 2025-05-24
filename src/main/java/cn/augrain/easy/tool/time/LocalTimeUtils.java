package cn.augrain.easy.tool.time;

import java.time.DateTimeException;
import java.time.LocalTime;

/**
 * 时间工具类
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class LocalTimeUtils {

    public static final String NORM_TIME_PATTERN = "HH:mm:ss";
    public static final String PURE_TIME_PATTERN = "HHmmss";

    private LocalTimeUtils() {

    }

    /**
     * 两个时间中的最小值
     *
     * @param timeOne   时间1
     * @param timeOther 时间2
     * @return 最小值
     */
    public static LocalTime min(LocalTime timeOne, LocalTime timeOther) {
        return !timeOne.isAfter(timeOther) ? timeOne : timeOther;
    }

    /**
     * 两个时间中的最大值
     *
     * @param timeOne   时间1
     * @param timeOther 时间2
     * @return 最大值
     */
    public static LocalTime max(LocalTime timeOne, LocalTime timeOther) {
        return timeOne.isAfter(timeOther) ? timeOne : timeOther;
    }

    /**
     * time1 是否小于等于 time2
     *
     * @param timeOne   时间1
     * @param timeOther 时间2
     * @return true/false
     */
    public static boolean le(LocalTime timeOne, LocalTime timeOther) {
        return !timeOne.isAfter(timeOther);
    }

    /**
     * time1 是否大于等于 time2
     *
     * @param timeOne   时间1
     * @param timeOther 时间2
     * @return true/false
     */
    public static boolean ge(LocalTime timeOne, LocalTime timeOther) {
        return !timeOther.isBefore(timeOne);
    }

    /**
     * 两个时间段是否完全包含
     *
     * @param timeOneStart   时段1起始时间
     * @param timeOneEnd     时段1结束时间
     * @param timeOtherStart 时段2起始时间
     * @param timeOtherEnd   时段2结束时间
     * @return true完全重叠，false不完全重叠/部分重叠
     */
    public static boolean isPeriodContains(LocalTime timeOneStart, LocalTime timeOneEnd,
                                           LocalTime timeOtherStart, LocalTime timeOtherEnd) {
        if (!timeOneStart.isAfter(timeOtherStart) && !timeOneEnd.isBefore(timeOtherEnd)) {
            return true;
        }
        return !timeOtherStart.isAfter(timeOneStart) && !timeOtherEnd.isBefore(timeOneEnd);
    }

    /**
     * 判断时间是否重叠
     * 重叠条件为：start1 <= end2 && end1 >= start2
     *
     * @param timeOneStart   时段1起始时间
     * @param timeOneEnd     时段1结束时间
     * @param timeOtherStart 时段2起始时间
     * @param timeOtherEnd   时段2结束时间
     * @param isStrict       是否严格遵守不能重叠，例如如果为true 那么8：00-8：30 和8：30-9：00 时间段比较为true
     * @return true重叠，false不重叠
     **/
    public static boolean isOverlap(LocalTime timeOneStart, LocalTime timeOneEnd,
                                    LocalTime timeOtherStart, LocalTime timeOtherEnd, boolean isStrict) {
        if (timeOneStart.isAfter(timeOneEnd) || timeOtherStart.isAfter(timeOtherEnd)) {
            throw new DateTimeException("endDate must be great than or equal to startDate");
        }
        if (isStrict) {
            return !timeOneStart.isAfter(timeOtherEnd) && !timeOneEnd.isBefore(timeOtherStart);
        }
        return timeOneStart.isBefore(timeOtherEnd) && timeOneEnd.isAfter(timeOtherStart);
    }

    /**
     * 是否是全天
     *
     * @param openStart 开始时间
     * @param openEnd   结束时间
     */
    public static boolean isAllDay(LocalTime openStart, LocalTime openEnd) {
        if (openStart == null || openEnd == null) {
            return false;
        }

        if (openStart.equals(openEnd)) {
            return true;
        }

        return openStart.equals(LocalTime.of(0, 0)) && openEnd.equals(LocalTime.of(23, 59));
    }

}
