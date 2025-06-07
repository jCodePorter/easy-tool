package cn.augrain.easy.tool.security;

import cn.augrain.easy.tool.lang.HexUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * DigestUtils
 *
 * @author biaoy
 * @since 2025/05/29
 */
public class DigestUtils {

    // from java 8
    public static final String MD2 = "MD2";
    public static final String MD5 = "MD5";
    public static final String SHA_1 = "SHA-1";
    public static final String SHA_224 = "SHA-224";
    public static final String SHA_256 = "SHA-256";
    public static final String SHA_384 = "SHA-384";
    public static final String SHA_512 = "SHA-512";

    // from java 9
    public static final String SHA3_224 = "SHA3-224";
    public static final String SHA3_256 = "SHA3-256";
    public static final String SHA3_384 = "SHA3-384";
    public static final String SHA3_512 = "SHA3-512";

    // from java 11
    public static final String SHA_512_224 = "SHA-512/224";
    public static final String SHA_512_256 = "SHA-512/256";

    /**
     * 计算字符串的MD5摘要
     *
     * @param input 输入字符串
     * @return MD5摘要的十六进制字符串
     */
    public static String md5(String input) {
        return digest(input, MD5);
    }

    /**
     * 计算文件的MD5摘要
     *
     * @param file 输入文件
     * @return MD5摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String md5(File file) throws IOException {
        return digest(file, MD5);
    }

    /**
     * 计算字符串的SHA-1摘要
     *
     * @param input 输入字符串
     * @return SHA-1摘要的十六进制字符串
     */
    public static String sha1(String input) {
        return digest(input, SHA_1);
    }

    /**
     * 计算文件的SHA-1摘要
     *
     * @param file 输入文件
     * @return SHA-1摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String sha1(File file) throws IOException {
        return digest(file, SHA_1);
    }

    /**
     * 计算字符串的SHA-256摘要
     *
     * @param input 输入字符串
     * @return SHA-256摘要的十六进制字符串
     */
    public static String sha256(String input) {
        return digest(input, SHA_256);
    }

    /**
     * 计算文件的SHA-256摘要
     *
     * @param file 输入文件
     * @return SHA-256摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String sha256(File file) throws IOException {
        return digest(file, SHA_256);
    }

    /**
     * 计算字符串的摘要
     *
     * @param input     输入字符串
     * @param algorithm 摘要算法（如MD5、SHA-1等）
     * @return 摘要的十六进制字符串
     */
    public static String digest(String input, String algorithm) {
        if (input == null) {
            throw new IllegalArgumentException("Input cannot be null");
        }
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] digestBytes = md.digest(input.getBytes());
            return HexUtils.byteToHex(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not supported: " + algorithm, e);
        }
    }

    /**
     * 计算文件的摘要
     *
     * @param file      输入文件
     * @param algorithm 摘要算法（如MD5、SHA-1等）
     * @return 摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String digest(File file, String algorithm) throws IOException {
        if (file == null || !file.exists()) {
            throw new IllegalArgumentException("File not exist");
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            byte[] buffer = new byte[4096];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                md.update(buffer, 0, bytesRead);
            }

            byte[] digestBytes = md.digest();
            return HexUtils.byteToHex(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithm not supported: " + algorithm, e);
        }
    }
}
