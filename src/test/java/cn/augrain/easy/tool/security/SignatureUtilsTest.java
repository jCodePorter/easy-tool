package cn.augrain.easy.tool.security;

import org.junit.Test;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * @author biaoy
 * @since 2025/05/31
 */
public class SignatureUtilsTest {

    @Test
    public void testRsa() {
        // 生成RSA密钥对
        KeyPair rsaKeyPair = SignatureUtils.generateRsaKeyPair(SignatureUtils.RSA_2048);
        PrivateKey rsaPrivateKey = rsaKeyPair.getPrivate();
        PublicKey rsaPublicKey = rsaKeyPair.getPublic();

        String data = "This is a signed message";

        // 使用SHA256withRSA签名
        byte[] signature = SignatureUtils.sign(SignatureUtils.RSA_SHA256, rsaPrivateKey, data.getBytes());
        System.out.println("Signature: " + Base64.getEncoder().encodeToString(signature));

        // 验证签名
        boolean isValid = SignatureUtils.verify(SignatureUtils.RSA_SHA256, rsaPublicKey, data.getBytes(), signature);
        System.out.println("Signature valid: " + isValid);

        // 篡改数据后的验证
        String tamperedData = "This is a tampered message";
        boolean isTamperedValid = SignatureUtils.verify(SignatureUtils.RSA_SHA256, rsaPublicKey, tamperedData.getBytes(), signature);
        System.out.println("Tampered signature valid: " + isTamperedValid);
    }
}
