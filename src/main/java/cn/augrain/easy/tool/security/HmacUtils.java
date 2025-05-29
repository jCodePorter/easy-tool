package cn.augrain.easy.tool.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * HMAC 工具类，用于 API 认证和消息验证
 *
 * @author biaoy
 * @since 2025/05/29
 */
public class HmacUtils {

    // from java 8
    public static final String HMAC_MD5 = "HmacMD5";
    public static final String HMAC_SHA1 = "HmacSHA1";
    public static final String HMAC_SHA224 = "HmacSHA224";
    public static final String HMAC_SHA256 = "HmacSHA256";
    public static final String HMAC_SHA384 = "HmacSHA384";
    public static final String HMAC_SHA512 = "HmacSHA512";

    // from java 9
    public static final String HMAC_SHA3_224 = "HmacSHA3-224";
    public static final String HMAC_SHA3_256 = "HmacSHA3-256";
    public static final String HMAC_SHA3_384 = "HmacSHA3-384";
    public static final String HMAC_SHA3_512 = "HmacSHA3-512";

    private HmacUtils() {
    }

    /**
     * 计算 HMAC
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥
     * @param data      数据
     * @return HMAC 字节数组
     */
    public static byte[] calculateHmac(String algorithm, byte[] secretKey, byte[] data) {
        try {
            Mac mac = Mac.getInstance(algorithm);
            SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, algorithm);
            mac.init(secretKeySpec);
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Unsupported algorithm: " + algorithm, e);
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("Invalid key for algorithm: " + algorithm, e);
        }
    }

    /**
     * 计算 HMAC（字符串密钥）
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字符串
     * @param data      数据字符串
     * @return HMAC 字节数组
     */
    public static byte[] calculateHmac(String algorithm, String secretKey, String data) {
        return calculateHmac(
                algorithm,
                secretKey.getBytes(StandardCharsets.UTF_8),
                data.getBytes(StandardCharsets.UTF_8)
        );
    }

    /**
     * 计算 HMAC 十六进制字符串
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字节数组
     * @param data      数据字节数组
     * @return HMAC 十六进制字符串
     */
    public static String hmacHex(String algorithm, byte[] secretKey, byte[] data) {
        return HexUtils.byteToHex(calculateHmac(algorithm, secretKey, data));
    }

    /**
     * 计算 HMAC 十六进制字符串（字符串密钥）
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字符串
     * @param data      数据字符串
     * @return HMAC 十六进制字符串
     */
    public static String hmacHex(String algorithm, String secretKey, String data) {
        return HexUtils.byteToHex(calculateHmac(algorithm, secretKey, data));
    }

    /**
     * 计算 HMAC Base64 字符串
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字节数组
     * @param data      数据字节数组
     * @return HMAC Base64 字符串
     */
    public static String hmacBase64(String algorithm, byte[] secretKey, byte[] data) {
        return Base64.getEncoder().encodeToString(calculateHmac(algorithm, secretKey, data));
    }

    /**
     * 计算 HMAC Base64 字符串（字符串密钥）
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字符串
     * @param data      数据字符串
     * @return HMAC Base64 字符串
     */
    public static String hmacBase64(String algorithm, String secretKey, String data) {
        return Base64.getEncoder().encodeToString(calculateHmac(algorithm, secretKey, data));
    }

    /**
     * 验证 HMAC
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字节数组
     * @param data      数据字节数组
     * @param hmac      待验证的 HMAC 字节数组
     * @return 验证结果
     */
    public static boolean verifyHmac(String algorithm, byte[] secretKey, byte[] data, byte[] hmac) {
        byte[] calculated = calculateHmac(algorithm, secretKey, data);
        return constantTimeEquals(calculated, hmac);
    }

    /**
     * 验证 HMAC 十六进制字符串
     *
     * @param algorithm 算法名称
     * @param secretKey 密钥字节数组
     * @param data      数据字节数组
     * @param hexHmac   待验证的 HMAC 十六进制字符串
     * @return 验证结果
     */
    public static boolean verifyHexHmac(String algorithm, String secretKey, String data, String hexHmac) {
        byte[] calculated = calculateHmac(algorithm, secretKey, data);
        byte[] received = HexUtils.hexToByte(hexHmac);
        return constantTimeEquals(calculated, received);
    }

    public static boolean verifyHexHmac(String algorithm, byte[] secretKey, byte[] data, String hexHmac) {
        byte[] calculated = calculateHmac(algorithm, secretKey, data);
        byte[] received = HexUtils.hexToByte(hexHmac);
        return constantTimeEquals(calculated, received);
    }

    /**
     * 验证 HMAC Base64 字符串
     *
     * @param algorithm  算法名称
     * @param secretKey  密钥字节数组
     * @param data       数据字节数组
     * @param base64Hmac 待验证的 HMAC Base64 字符串
     * @return 验证结果
     */
    public static boolean verifyBase64Hmac(String algorithm, byte[] secretKey, byte[] data, String base64Hmac) {
        byte[] calculated = calculateHmac(algorithm, secretKey, data);
        byte[] received = Base64.getDecoder().decode(base64Hmac);
        return constantTimeEquals(calculated, received);
    }

    /**
     * 安全比较两个字节数组（防止时序攻击）
     *
     * @param a 第一个字节数组
     * @param b 第二个字节数组
     * @return 是否相等
     */
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }

        if (a.length != b.length) {
            return false;
        }

        int result = 0;
        for (int i = 0; i < a.length; i++) {
            result |= a[i] ^ b[i];
        }
        return result == 0;
    }

    /**
     * 为 API 认证生成标准认证头
     *
     * @param apiKey    API Key
     * @param secretKey 密钥
     * @param data      请求数据
     * @param algorithm 算法（默认为 HmacSHA256）
     * @return 认证头字符串
     */
    public static String generateAuthHeader(String apiKey, String secretKey, String data, String algorithm) {
        if (algorithm == null || algorithm.isEmpty()) {
            algorithm = HMAC_SHA256;
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        String payload = apiKey + timestamp + data;

        String signature = hmacHex(algorithm, secretKey, payload);

        return String.format(
                "HMAC %s:%s:%s",
                apiKey,
                timestamp,
                signature
        );
    }

    /**
     * 验证 API 认证头
     *
     * @param authHeader 认证头
     * @param secretKey  密钥
     * @param data       请求数据
     * @param algorithm  算法（默认为 HmacSHA256）
     * @return 验证结果
     */
    public static boolean verifyAuthHeader(String authHeader, String secretKey, String data, String algorithm) {
        if (authHeader == null || !authHeader.startsWith("HMAC ")) {
            return false;
        }

        if (algorithm == null || algorithm.isEmpty()) {
            algorithm = HMAC_SHA256;
        }

        String[] parts = authHeader.substring(5).split(":");
        if (parts.length != 3) {
            return false;
        }

        String apiKey = parts[0];
        String timestamp = parts[1];
        String receivedSignature = parts[2];

        // 验证时间戳有效性（示例：5分钟内有效）
        long time = Long.parseLong(timestamp);
        if (Math.abs(System.currentTimeMillis() - time) > 5 * 60 * 1000) {
            return false;
        }

        String payload = apiKey + timestamp + data;
        String calculatedSignature = hmacHex(algorithm, secretKey, payload);

        return constantTimeEquals(
                calculatedSignature.getBytes(StandardCharsets.UTF_8),
                receivedSignature.getBytes(StandardCharsets.UTF_8)
        );
    }
}