package cn.augrain.easy.tool.time;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * 日期相关工具类
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class LocalDateUtils {

    public static final String NORM_DATE_PATTERN = "yyyy-MM-dd";
    public static final String PURE_DATE_PATTERN = "yyyyMMdd";

    private LocalDateUtils() {

    }

    /**
     * 计算两个日期之间的天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    /**
     * 获取指定日期所在月份的第一天
     *
     * @param date 指定日期
     * @return 所在月份的第一天
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    /**
     * 获取指定日期所在月份的最后一天
     *
     * @param date 指定日期
     * @return 所在月份的最后一天
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    /**
     * 判断日期是否在指定范围内
     *
     * @param date      指定日期
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 是否包含在范围内
     */
    public static boolean isDateInRange(LocalDate date, LocalDate startDate, LocalDate endDate) {
        return date.isAfter(startDate) && date.isBefore(endDate);
    }

    /**
     * 判断是否为闰年
     *
     * @param year 年
     * @return true/false
     */
    public static boolean isLeapYear(int year) {
        return Year.of(year).isLeap();
    }

    /**
     * 获取指定月份的天数
     *
     * @param year  年
     * @param month 月
     * @return 当月的天数
     */
    public static int getDaysInMonth(int year, int month) {
        return YearMonth.of(year, month).lengthOfMonth();
    }

    /**
     * 获取指定日期的季度
     *
     * @param date 指定日期
     * @return 季度
     */
    public static int getQuarter(LocalDate date) {
        return (date.getMonthValue() - 1) / 3 + 1;
    }

    /**
     * 获取指定日期的下一个工作日
     *
     * @param date 指定日期
     * @return 下一个工作日
     */
    public static LocalDate getNextWorkingDay(LocalDate date) {
        do {
            date = date.plusDays(1);
        } while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
        return date;
    }

    /**
     * 获取指定日期的上一个工作日
     *
     * @param date 指定日期
     * @return 上一个工作日
     */
    public static LocalDate getPreviousWorkingDay(LocalDate date) {
        do {
            date = date.minusDays(1);
        } while (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY);
        return date;
    }

    /**
     * 获取指定日期所在周的第一天（周一）
     *
     * @param date 指定日期
     * @return 所在周的第一天
     */
    public static LocalDate getFirstDayOfWeek(LocalDate date) {
        return date.with(DayOfWeek.MONDAY);
    }

    /**
     * 获取指定日期所在周的最后一天（周日）
     *
     * @param date 指定日期
     * @return 所在周的最后一天
     */
    public static LocalDate getLastDayOfWeek(LocalDate date) {
        return date.with(DayOfWeek.SUNDAY);
    }

    /**
     * 获取指定日期所在年的第一天
     *
     * @param date 指定日期
     * @return 所在年的第一天
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        return date.withDayOfYear(1);
    }

    /**
     * 获取指定日期所在年的最后一天
     *
     * @param date 指定日期
     * @return 所在年最后一天
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        return date.withDayOfYear(date.lengthOfYear());
    }

    /**
     * 获取指定日期所在季度的第一天
     *
     * @param date 指定日期
     * @return 本季度第一天
     */
    public static LocalDate getFirstDayOfQuarter(LocalDate date) {
        int month = (date.getMonthValue() - 1) / 3 * 3 + 1;
        return LocalDate.of(date.getYear(), month, 1);
    }

    /**
     * 获取指定日期所在季度的最后一天
     *
     * @param date 指定日期
     * @return 本季度最后一天
     */
    public static LocalDate getLastDayOfQuarter(LocalDate date) {
        int month = (date.getMonthValue() - 1) / 3 * 3 + 3;
        return LocalDate.of(date.getYear(), month, Month.of(month).maxLength());
    }

    /**
     * 判断指定日期是否为工作日（周一至周五）
     *
     * @param date 指定日期
     * @return 是否为工作日
     */
    public static boolean isWeekday(LocalDate date) {
        return date.getDayOfWeek() != DayOfWeek.SATURDAY && date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    /**
     * 判断指定日期是否为周末（周六或周日）
     *
     * @param date 指定日期
     * @return 是否为周末
     */
    public static boolean isWeekend(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    /**
     * 获取指定日期所在月份的工作日天数
     *
     * @param date 指定日期
     * @return 天数
     */
    public static int getWeekdayCountOfMonth(LocalDate date) {
        int weekdayCount = 0;
        LocalDate firstDayOfMonth = getFirstDayOfMonth(date);
        LocalDate lastDayOfMonth = getLastDayOfMonth(date);
        while (!firstDayOfMonth.isAfter(lastDayOfMonth)) {
            if (isWeekday(firstDayOfMonth)) {
                weekdayCount++;
            }
            firstDayOfMonth = firstDayOfMonth.plusDays(1);
        }
        return weekdayCount;
    }

    /**
     * 获取指定日期所在月份的周末天数
     *
     * @param date 指定日期
     * @return 天数
     */
    public static int getWeekendCountOfMonth(LocalDate date) {
        int weekendCount = 0;
        LocalDate firstDayOfMonth = getFirstDayOfMonth(date);
        LocalDate lastDayOfMonth = getLastDayOfMonth(date);
        while (!firstDayOfMonth.isAfter(lastDayOfMonth)) {
            if (isWeekend(firstDayOfMonth)) {
                weekendCount++;
            }
            firstDayOfMonth = firstDayOfMonth.plusDays(1);
        }
        return weekendCount;
    }

    /**
     * 获取指定日期所在年份的工作日天数
     *
     * @param date 指定日期
     * @return 天数
     */
    public static int getWeekdayCountOfYear(LocalDate date) {
        int weekdayCount = 0;
        LocalDate firstDayOfYear = getFirstDayOfYear(date);
        LocalDate lastDayOfYear = getLastDayOfYear(date);
        while (!firstDayOfYear.isAfter(lastDayOfYear)) {
            if (isWeekday(firstDayOfYear)) {
                weekdayCount++;
            }
            firstDayOfYear = firstDayOfYear.plusDays(1);
        }
        return weekdayCount;
    }
}
