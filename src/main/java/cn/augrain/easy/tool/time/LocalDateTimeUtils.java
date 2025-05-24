package cn.augrain.easy.tool.time;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class LocalDateTimeUtils {

    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String PURE_DATETIME_MS_PATTERN = "yyyyMMddHHmmssSSS";

    private LocalDateTimeUtils() {

    }

    /**
     * 获取时间戳（毫秒数）
     *
     * @return 返回以毫秒为单位时间戳
     */
    public static Long getNowMillis() {
        return LocalDateTime.now().toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 时间转时间戳(毫秒）
     *
     * @param localDateTime 时间
     * @return 时间戳
     */
    public static long toEpochMilli(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.toEpochMilli();
    }

    /**
     * 时间戳转时间
     *
     * @param timestamp 时间戳
     * @return 时间
     */
    public static LocalDateTime fromEpochSecond(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(timestamp), ZoneId.systemDefault());
    }

    /**
     * 时间转时间戳（秒）
     *
     * @param localDateTime 时间
     * @return 时间戳
     */
    public static long toEpochSecond(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return instant.getEpochSecond();
    }

    public static Date toDate(LocalDateTime dateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = dateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static Calendar toCalendar(LocalDateTime dateTime) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(toDate(dateTime));
        return instance;
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        Instant instant = date.toInstant();
        ZoneId zoneId = ZoneId.systemDefault();
        return LocalDateTime.ofInstant(instant, zoneId);
    }

    /**
     * 解析为LocalDateTime
     *
     * @param dateStr 日期字符串
     * @return LocalDateTime
     */
    public static LocalDateTime parse(String dateStr) {
        return parse(dateStr, NORM_DATETIME_PATTERN);
    }

    public static LocalDateTime parse(String date, String pattern) {
        return LocalDateTime.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(dateTime);
    }

    /**
     * 判断时间是否重叠
     * 重叠条件为：start1 <= end2 && end1 >= start2
     *
     * @param start1   时段1起始时间
     * @param end1     时段1结束时间
     * @param start2   时段2起始时间
     * @param end2     时段2结束时间
     * @param isStrict 是否严格遵守不能重叠，例如如果为true 那么8：00-8：30 和8：30-9：00 时间段比较为true
     * @return true重叠，false不重叠
     **/
    public static boolean isOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2, boolean isStrict) {
        if (start1.isAfter(end1) || start2.isAfter(end2)) {
            throw new DateTimeException("endDate不能小于startDate");
        }
        if (isStrict) {
            return !start1.isAfter(end2) && !end1.isBefore(start2);
        } else {
            return start1.isBefore(end2) && end1.isAfter(start2);
        }
    }
}
