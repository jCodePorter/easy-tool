package cn.augrain.easy.tool.timewheel;

import cn.augrain.easy.tool.algorithm.timewheel.AdaptiveTimeWheel;
import cn.augrain.easy.tool.algorithm.timewheel.TaskStopConditions;
import cn.augrain.easy.tool.time.LocalDateTimeUtils;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static cn.augrain.easy.tool.algorithm.timewheel.TaskStopConditions.any;

public class TimeWheelTest {

    @Test
    public void test() throws Exception {
        AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();
        timeWheel.addTask(() -> {
            System.out.println(String.format("[%s] execute #%d", LocalDateTimeUtils.nowStr(), 120));
        }, 60);

        Thread.currentThread().join();
    }

    @Test
    public void testBasicTask() throws Exception {
        AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();
        System.out.println(String.format("create task at %s", LocalDateTimeUtils.nowStr()));

        for (int i = 1; i <= 20; i++) {
            final int number = i;
            timeWheel.addTask(() -> {
                System.out.println(String.format("[%s] execute #%d", LocalDateTimeUtils.nowStr(), number));
            }, number);
        }

        Thread.currentThread().join();
    }

    @Test
    public void testBasicRepeatingTask() throws Exception {
        AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();
        AtomicInteger counter = new AtomicInteger(0);

        System.out.printf("添加定时任务，每2秒执行一次，当前时间: %s \n", LocalDateTimeUtils.nowStr());
        timeWheel.addRepeatingTask(() -> {
            int count = counter.incrementAndGet();
            System.out.println(String.format("[%s] execute #%d", LocalDateTimeUtils.nowStr(), count));
        }, 1);

        Thread.currentThread().join();
    }

    @Test
    public void testRepeatingSpecificTimesTask() throws Exception {
        AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();
        AtomicInteger counter = new AtomicInteger(0);

        System.out.printf("添加定时任务，每1秒执行一次，共执行10次，当前时间: %s \n", LocalDateTimeUtils.nowStr());
        timeWheel.addRepeatingTask(() -> {
            int count = counter.incrementAndGet();
            System.out.println(String.format("[%s] execute #%d", LocalDateTimeUtils.nowStr(), count));
        }, 1, 10);

        Thread.currentThread().join();
    }

    @Test
    public void testConditionalStopTask() throws Exception {
        AdaptiveTimeWheel timeWheel = new AdaptiveTimeWheel();
        AtomicInteger timeCounter = new AtomicInteger(0);
        AtomicInteger countCounter = new AtomicInteger(0);

        // 10s 以后停止执行
        String taskId1 = timeWheel.addRepeatingTask(() -> {
            int count = timeCounter.incrementAndGet();
            System.out.println(String.format("task1 [%s] execute #%d", LocalDateTimeUtils.nowStr(), count));
        }, 1, TaskStopConditions.TimeBasedStopCondition.afterSeconds(10));

        // 执行5后停止
        String taskId2 = timeWheel.addRepeatingTask(() -> {
            int count = countCounter.incrementAndGet();
            System.out.println(String.format("task2 [%s] execute #%d", LocalDateTimeUtils.nowStr(), count));
        }, 2, new TaskStopConditions.ExecutionCountStopCondition(5));

        // 执行4次或者8s停止
        AtomicInteger complexCounter = new AtomicInteger(0);
        String taskId3 = timeWheel.addRepeatingTask(() -> {
            int count = complexCounter.incrementAndGet();
            System.out.println(String.format("task3 [%s] execute #%d", LocalDateTimeUtils.nowStr(), count));
        }, 3, any(
                TaskStopConditions.TimeBasedStopCondition.afterSeconds(8),
                new TaskStopConditions.ExecutionCountStopCondition(4)
        ));

        Thread.currentThread().join();
    }
}
