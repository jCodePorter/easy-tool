package cn.augrain.easy.tool.core;

import cn.augrain.easy.tool.consts.StrConst;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * 字符串工具测试类
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class StringUtilsTest {

    @Test
    public void testEscape() {
        String str = "http://www.baidu.com?q=123";

        String escape = StringUtils.escape(str);
        String unescape = StringUtils.unescape(escape);

        Assert.assertEquals(str, unescape);
    }

    @Test
    public void testSplitTo() {
        String str = "1,2,3,4,5";
        List<Long> longs = StringUtils.splitToLong(str);
        System.out.println(longs);
    }
}
