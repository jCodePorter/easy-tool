package cn.augrain.easy.tool.security;

import cn.augrain.easy.tool.exception.SignatureException;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

/**
 * 数字签名工具类
 *
 * @author biaoy
 * @since 2025/05/31
 */
public class SignatureUtils {

    // 算法常量
    public static final String RSA_SHA256 = "SHA256withRSA";
    public static final String RSA_SHA512 = "SHA512withRSA";
    public static final String RSA_SHA384 = "SHA384withRSA";
    public static final String RSA_PSS_SHA256 = "SHA256withRSAandMGF1";
    public static final String ECDSA_SHA256 = "SHA256withECDSA";
    public static final String ECDSA_SHA384 = "SHA384withECDSA";
    public static final String ECDSA_SHA512 = "SHA512withECDSA";
    public static final String DSA_SHA256 = "SHA256withDSA";

    // 密钥长度
    public static final int RSA_2048 = 2048;
    public static final int RSA_3072 = 3072;
    public static final int RSA_4096 = 4096;
    public static final int EC_256 = 256;
    public static final int EC_384 = 384;
    public static final int EC_521 = 521;
    public static final int DSA_2048 = 2048;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private SignatureUtils() {
    }

    /**
     * 生成数字签名
     *
     * @param algorithm  签名算法
     * @param privateKey 私钥
     * @param data       原始数据
     * @return 签名字节数组
     */
    public static byte[] sign(String algorithm, PrivateKey privateKey, byte[] data) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initSign(privateKey, SECURE_RANDOM);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new SignatureException("Signature generation failed", e);
        }
    }

    /**
     * 验证数字签名
     *
     * @param algorithm      签名算法
     * @param publicKey      公钥
     * @param data           原始数据
     * @param signatureBytes 签名字节数组
     * @return 验证结果
     */
    public static boolean verify(String algorithm, PublicKey publicKey, byte[] data, byte[] signatureBytes) {
        try {
            Signature signature = Signature.getInstance(algorithm);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new SignatureException("Signature verification failed", e);
        }
    }

    /**
     * 生成RSA-PSS签名
     *
     * @param privateKey 私钥
     * @param data       原始数据
     * @param saltLength 盐长度
     * @return 签名字节数组
     */
    public static byte[] signRsaPss(PrivateKey privateKey, byte[] data, int saltLength) {
        try {
            Signature signature = Signature.getInstance(RSA_PSS_SHA256);
            PSSParameterSpec pssSpec = new PSSParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, saltLength, 1
            );
            signature.setParameter(pssSpec);
            signature.initSign(privateKey, SECURE_RANDOM);
            signature.update(data);
            return signature.sign();
        } catch (Exception e) {
            throw new SignatureException("RSA-PSS signature generation failed", e);
        }
    }

    /**
     * 验证RSA-PSS签名
     *
     * @param publicKey      公钥
     * @param data           原始数据
     * @param signatureBytes 签名字节数组
     * @param saltLength     盐长度
     * @return 验证结果
     */
    public static boolean verifyRsaPss(PublicKey publicKey, byte[] data, byte[] signatureBytes, int saltLength) {
        try {
            Signature signature = Signature.getInstance(RSA_PSS_SHA256);
            PSSParameterSpec pssSpec = new PSSParameterSpec(
                    "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, saltLength, 1
            );
            signature.setParameter(pssSpec);
            signature.initVerify(publicKey);
            signature.update(data);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            throw new SignatureException("RSA-PSS signature verification failed", e);
        }
    }

}
