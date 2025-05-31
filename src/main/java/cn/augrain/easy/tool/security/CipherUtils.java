package cn.augrain.easy.tool.security;

import cn.augrain.easy.tool.exception.CryptoException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

/**
 * 加密解密工具类
 *
 * @author biaoy
 * @since 2025/05/31
 */
public class CipherUtils {
    // 算法常量
    public static final String AES = "AES";
    public static final String AES_CBC_PKCS5 = "AES/CBC/PKCS5Padding";
    public static final String AES_GCM_NOPAD = "AES/GCM/NoPadding";
    public static final String DES = "DES";
    public static final String DESEDE = "DESede";
    public static final String RSA = "RSA";
    public static final String RSA_ECB_PKCS1 = "RSA/ECB/PKCS1Padding";
    public static final String RSA_ECB_OAEP = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding";
    public static final String EC = "EC";

    // 密钥长度
    public static final int AES_128 = 128;
    public static final int AES_192 = 192;
    public static final int AES_256 = 256;
    public static final int DES_56 = 56;
    public static final int DESEDE_112 = 112;
    public static final int DESEDE_168 = 168;
    public static final int RSA_2048 = 2048;
    public static final int RSA_3072 = 3072;
    public static final int RSA_4096 = 4096;

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 16;

    private CipherUtils() {
        // 工具类，防止实例化
    }

    // ================== 对称加密 ================== //

    /**
     * 生成对称密钥
     *
     * @param algorithm 算法
     * @param keySize   密钥长度（位）
     * @return 密钥字节数组
     */
    public static byte[] generateSymmetricKey(String algorithm, int keySize) {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance(algorithm);
            keyGen.init(keySize, SECURE_RANDOM);
            return keyGen.generateKey().getEncoded();
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("Failed to generate key", e);
        }
    }

    /**
     * AES加密（CBC模式）
     *
     * @param data 明文数据
     * @param key  密钥
     * @param iv   初始化向量
     * @return 密文
     */
    public static byte[] aesEncrypt(byte[] data, byte[] key, byte[] iv) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptoException("AES encryption failed", e);
        }
    }

    /**
     * AES解密（CBC模式）
     *
     * @param encrypted 密文
     * @param key       密钥
     * @param iv        初始化向量
     * @return 明文
     */
    public static byte[] aesDecrypt(byte[] encrypted, byte[] key, byte[] iv) {
        try {
            SecretKeySpec keySpec = new SecretKeySpec(key, AES);
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new CryptoException("AES decryption failed", e);
        }
    }

    /**
     * AES-GCM加密
     *
     * @param data 明文数据
     * @param key  密钥
     * @return 包含IV、密文和认证标签的Map
     */
    public static Map<String, byte[]> aesGcmEncrypt(byte[] data, byte[] key) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            SECURE_RANDOM.nextBytes(iv);

            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, AES);

            Cipher cipher = Cipher.getInstance(AES_GCM_NOPAD);
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, spec);

            byte[] encrypted = cipher.doFinal(data);

            Map<String, byte[]> result = new HashMap<>();
            result.put("iv", iv);
            result.put("ciphertext", encrypted);
            return result;
        } catch (Exception e) {
            throw new CryptoException("AES-GCM encryption failed", e);
        }
    }

    /**
     * AES-GCM解密
     *
     * @param encrypted 密文
     * @param key       密钥
     * @param iv        初始化向量
     * @return 明文
     */
    public static byte[] aesGcmDecrypt(byte[] encrypted, byte[] key, byte[] iv) {
        try {
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH * 8, iv);
            SecretKeySpec keySpec = new SecretKeySpec(key, AES);

            Cipher cipher = Cipher.getInstance(AES_GCM_NOPAD);
            cipher.init(Cipher.DECRYPT_MODE, keySpec, spec);

            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new CryptoException("AES-GCM decryption failed", e);
        }
    }

    // ================== 非对称加密 ================== //

    /**
     * RSA加密
     *
     * @param data      明文数据
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] rsaEncrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptoException("RSA encryption failed", e);
        }
    }

    /**
     * RSA解密
     *
     * @param encrypted  密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static byte[] rsaDecrypt(byte[] encrypted, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new CryptoException("RSA decryption failed", e);
        }
    }

    /**
     * RSA-OAEP加密（更安全）
     *
     * @param data      明文数据
     * @param publicKey 公钥
     * @return 密文
     */
    public static byte[] rsaOaepEncrypt(byte[] data, PublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_OAEP);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data);
        } catch (Exception e) {
            throw new CryptoException("RSA-OAEP encryption failed", e);
        }
    }

    /**
     * RSA-OAEP解密
     *
     * @param encrypted  密文
     * @param privateKey 私钥
     * @return 明文
     */
    public static byte[] rsaOaepDecrypt(byte[] encrypted, PrivateKey privateKey) {
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_OAEP);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(encrypted);
        } catch (Exception e) {
            throw new CryptoException("RSA-OAEP decryption failed", e);
        }
    }
}
