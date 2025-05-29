package cn.augrain.easy.tool.digest;

import cn.augrain.easy.tool.security.DigestUtils;
import org.junit.Test;

/**
 * DigestUtilsTest
 *
 * @author biaoy
 * @since 2025/05/29
 */
public class DigestUtilsTest {

    @Test
    public void testSha1() {
        String test = "haha";

        System.out.println(DigestUtils.sha1(test));
    }
}
