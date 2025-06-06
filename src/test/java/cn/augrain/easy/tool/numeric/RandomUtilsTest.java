package cn.augrain.easy.tool.numeric;

import org.junit.Test;

public class RandomUtilsTest {

    @Test
    public void testRandomStr() {
        System.out.println(RandomUtils.randomString(10));
        System.out.println(RandomUtils.randomCharString(10));
    }
}
