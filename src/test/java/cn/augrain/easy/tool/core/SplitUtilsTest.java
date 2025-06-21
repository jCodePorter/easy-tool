package cn.augrain.easy.tool.core;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author biaoy
 * @since 2025/06/21
 */
public class SplitUtilsTest {

    @Test
    public void testSplitTo() {
        String str = "1,2,3,4,5";
        List<Long> longs = SplitUtils.splitToLong(str);
        System.out.println(longs);
    }

    @Test
    public void splitToLongArray() {
        String str = "1,2,3,4";

        Long[] longs = SplitUtils.splitToArray(str, ",", Long.class);

        System.out.println(Arrays.toString(longs));
    }
}
