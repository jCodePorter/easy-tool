package com.bytefuture.easy.tool.time;

import java.time.LocalDate;

import static com.bytefuture.easy.tool.time.TemporalUtils.isBetween;

/**
 * @author biaoy
 * @since 2026/02/03
 */
public class TemporalTest {

    // 测试示例
    public static void main(String[] args) {
        LocalDate start = LocalDate.of(2026, 1, 1);
        LocalDate end = LocalDate.of(2026, 12, 31);

        // 边界值测试
        System.out.println(isBetween(start, start, end));  // true
        System.out.println(isBetween(end, start, end));    // true
        System.out.println(isBetween(LocalDate.of(2026, 6, 15), start, end));  // true
        System.out.println(isBetween(LocalDate.of(2025, 12, 31), start, end)); // false

        // 开放区间测试
        System.out.println(isBetween(start, start, end, false, true));  // false (start, end]
        System.out.println(isBetween(start, null, end, true, true)); // true [start, end]
    }
}
