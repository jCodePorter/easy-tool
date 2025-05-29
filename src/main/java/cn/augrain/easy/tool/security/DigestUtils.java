package cn.augrain.easy.tool.security;

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

    /**
     * 计算字符串的MD5摘要
     *
     * @param input 输入字符串
     * @return MD5摘要的十六进制字符串
     */
    public static String md5(String input) {
        return digest(input, "MD5");
    }

    /**
     * 计算文件的MD5摘要
     *
     * @param file 输入文件
     * @return MD5摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String md5(File file) throws IOException {
        return digest(file, "MD5");
    }

    /**
     * 计算字符串的SHA-1摘要
     *
     * @param input 输入字符串
     * @return SHA-1摘要的十六进制字符串
     */
    public static String sha1(String input) {
        return digest(input, "SHA-1");
    }

    /**
     * 计算文件的SHA-1摘要
     *
     * @param file 输入文件
     * @return SHA-1摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String sha1(File file) throws IOException {
        return digest(file, "SHA-1");
    }

    /**
     * 计算字符串的SHA-256摘要
     *
     * @param input 输入字符串
     * @return SHA-256摘要的十六进制字符串
     */
    public static String sha256(String input) {
        return digest(input, "SHA-256");
    }

    /**
     * 计算文件的SHA-256摘要
     *
     * @param file 输入文件
     * @return SHA-256摘要的十六进制字符串
     * @throws IOException 如果读取文件出错
     */
    public static String sha256(File file) throws IOException {
        return digest(file, "SHA-256");
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
