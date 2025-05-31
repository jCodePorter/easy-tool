package cn.augrain.easy.tool.security;

import cn.augrain.easy.tool.exception.SignatureException;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

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
     * 生成RSA密钥对
     *
     * @param keySize 密钥长度
     * @return 密钥对
     */
    public static KeyPair generateRsaKeyPair(int keySize) {
        return getKeyPair("RSA", keySize);
    }

    /**
     * 生成EC密钥对
     *
     * @param curveSize 曲线大小
     * @return 密钥对
     */
    public static KeyPair generateEcKeyPair(int curveSize) {
        return getKeyPair("EC", curveSize);
    }

    /**
     * 生成DSA密钥对
     *
     * @param keySize 密钥长度
     * @return 密钥对
     */
    public static KeyPair generateDsaKeyPair(int keySize) {
        return getKeyPair("DSA", keySize);
    }

    private static KeyPair getKeyPair(String algorithm, int keySize) {
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algorithm);
            keyGen.initialize(keySize, SECURE_RANDOM);
            return keyGen.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new SignatureException(String.format("Failed to generate %s key pair", algorithm), e);
        }
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

    /**
     * 将公钥转换为Base64字符串
     *
     * @param publicKey 公钥
     * @return Base64编码字符串
     */
    public static String publicKeyToBase64(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    /**
     * 将私钥转换为Base64字符串
     *
     * @param privateKey 私钥
     * @return Base64编码字符串
     */
    public static String privateKeyToBase64(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    /**
     * 从Base64字符串恢复RSA公钥
     *
     * @param base64PublicKey Base64编码的公钥
     * @return 公钥对象
     */
    public static PublicKey rsaPublicKeyFromBase64(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new SignatureException("Failed to restore RSA public key", e);
        }
    }

    /**
     * 从Base64字符串恢复RSA私钥
     *
     * @param base64PrivateKey Base64编码的私钥
     * @return 私钥对象
     */
    public static PrivateKey rsaPrivateKeyFromBase64(String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new SignatureException("Failed to restore RSA private key", e);
        }
    }

    /**
     * 从Base64字符串恢复EC公钥
     *
     * @param base64PublicKey Base64编码的公钥
     * @return 公钥对象
     */
    public static PublicKey ecPublicKeyFromBase64(String base64PublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PublicKey);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePublic(spec);
        } catch (Exception e) {
            throw new SignatureException("Failed to restore EC public key", e);
        }
    }

    /**
     * 从Base64字符串恢复EC私钥
     *
     * @param base64PrivateKey Base64编码的私钥
     * @return 私钥对象
     */
    public static PrivateKey ecPrivateKeyFromBase64(String base64PrivateKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64PrivateKey);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(spec);
        } catch (Exception e) {
            throw new SignatureException("Failed to restore EC private key", e);
        }
    }

    /**
     * 从私钥获取公钥
     *
     * @param privateKey 私钥
     * @return 对应的公钥
     */
    private static PublicKey getPublicKeyFromPrivate(PrivateKey privateKey) {
        if (privateKey instanceof java.security.interfaces.RSAPrivateKey) {
            java.security.interfaces.RSAPrivateKey rsaPrivate = (java.security.interfaces.RSAPrivateKey) privateKey;
            try {
                RSAPublicKeySpec keySpec = new RSAPublicKeySpec(
                        rsaPrivate.getModulus(),
                        rsaPrivate instanceof java.security.interfaces.RSAPrivateCrtKey ?
                                ((java.security.interfaces.RSAPrivateCrtKey) rsaPrivate).getPublicExponent() :
                                // 对于非CRT密钥，使用标准指数65537
                                java.math.BigInteger.valueOf(65537)
                );
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(keySpec);
            } catch (Exception e) {
                throw new SignatureException("Failed to derive public key from RSA private key", e);
            }
        } else if (privateKey instanceof java.security.interfaces.ECPrivateKey) {
            java.security.interfaces.ECPrivateKey ecPrivate = (java.security.interfaces.ECPrivateKey) privateKey;
            try {
                ECParameterSpec params = ecPrivate.getParams();
                ECPoint publicPoint = new ECPoint(
                        ecPrivate.getParams().getGenerator().getAffineX(),
                        ecPrivate.getParams().getGenerator().getAffineY()
                );
                ECPublicKeySpec keySpec = new ECPublicKeySpec(publicPoint, params);
                KeyFactory keyFactory = KeyFactory.getInstance("EC");
                return keyFactory.generatePublic(keySpec);
            } catch (Exception e) {
                throw new SignatureException("Failed to derive public key from EC private key", e);
            }
        }
        throw new SignatureException("Unsupported private key type: " + privateKey.getAlgorithm());
    }

}
