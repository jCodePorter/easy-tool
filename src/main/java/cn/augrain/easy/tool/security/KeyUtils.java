package cn.augrain.easy.tool.security;

import cn.augrain.easy.tool.exception.SignatureException;

import java.security.*;
import java.security.spec.*;
import java.util.Base64;

/**
 * 秘钥生成工具
 *
 * @author biaoy
 * @since 2025/05/31
 */
public class KeyUtils {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

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
     * 将密钥转换为Base64字符串
     *
     * @param key 密钥
     * @return Base64编码字符串
     */
    public static String keyToBase64(Key key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
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

    /**
     * 生成随机IV
     *
     * @param length IV长度
     * @return IV字节数组
     */
    public static byte[] generateIv(int length) {
        byte[] iv = new byte[length];
        SECURE_RANDOM.nextBytes(iv);
        return iv;
    }
}
