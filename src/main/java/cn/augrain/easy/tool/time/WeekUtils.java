package cn.augrain.easy.tool.time;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

/**
 * 周工具类
 *
 * @author biaoy
 * @since 2026/01/11
 */
public class WeekUtils {

    /**
     * 获取指定日期所在周的指定星期几
     *
     * @param date      任意日期
     * @param dayOfWeek 星期几
     * @return 该周指定星期几的日期
     */
    public static LocalDate getDayOfWeek(LocalDate date, DayOfWeek dayOfWeek) {
        return date.with(TemporalAdjusters.nextOrSame(dayOfWeek));
    }

    /**
     * 判断两个日期是否在同一周
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否在同一周
     */
    public static boolean isSameWeek(LocalDate date1, LocalDate date2) {
        LocalDate monday1 = date1.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate monday2 = date2.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        return monday1.equals(monday2);
    }

    /**
     * 获取指定日期所在周的所有工作日
     *
     * @param date 任意日期
     * @return 周一到周五的日期列表
     */
    public static LocalDate[] getWorkdaysOfWeek(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate[] workdays = new LocalDate[5];

        for (int i = 0; i < 5; i++) {
            workdays[i] = monday.plusDays(i);
        }
        return workdays;
    }

    /**
     * 获取指定日期所在周的所有日期
     *
     * @param date 任意日期
     * @return 周一到周日的日期列表
     */
    public static LocalDate[] getAllDaysOfWeek(LocalDate date) {
        LocalDate monday = date.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate[] days = new LocalDate[7];

        for (int i = 0; i < 7; i++) {
            days[i] = monday.plusDays(i);
        }
        return days;
    }
}
