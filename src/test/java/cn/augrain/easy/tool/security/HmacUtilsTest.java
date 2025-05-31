package cn.augrain.easy.tool.security;

import org.junit.Test;

/**
 * HmacUtilsTest
 *
 * @author biaoy
 * @since 2025/05/29
 */
public class HmacUtilsTest {

    @Test
    public void testSha256() {
        String secret = "mySecretKey";
        String data = "Hello, HMAC!";

        // 计算 HMAC-SHA256
        String hmacSha256 = HmacUtils.hmacHex(HmacUtils.HMAC_SHA256, secret, data);
        System.out.println("HMAC-SHA256: " + hmacSha256);

        // 验证 HMAC
        boolean valid = HmacUtils.verifyHexHmac(HmacUtils.HMAC_SHA256, secret, data, hmacSha256);
        System.out.println("HMAC Verification: " + valid);
    }
}
