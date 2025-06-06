package cn.augrain.easy.tool.numeric;

import java.util.Random;

/**
 * 随机数生成
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class RandomUtils {

    private static final String ALL_CHAR = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LETTER_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String NUMBER_CHAR = "0123456789";

    /**
     * 生成随机的整数
     *
     * @param min 最小值
     * @param max 最大值
     * @return 随机值
     */
    public static int randomInt(int min, int max) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * 获取定长的随机数，包含大小写、数字
     *
     * @param length 随机数长度
     * @return 随机字符串
     */
    public static String randomString(int length) {
        return doRandom(length, ALL_CHAR);
    }

    /**
     * 获取定长的随机数,包含大小写字母
     *
     * @param length 随机数长度
     * @return 随机字符串
     */
    public static String randomCharString(int length) {
        return doRandom(length, LETTER_CHAR);
    }

    /**
     * 获取定长的随机数,只包含数字
     *
     * @param length 随机数长度
     * @return 随机字符串
     */
    public static String randomNumberString(int length) {
        return doRandom(length, NUMBER_CHAR);
    }

    private static String doRandom(int length, String letterChar) {
        Random random = new Random();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            builder.append(letterChar.charAt(random.nextInt(letterChar.length())));
        }
        return builder.toString();
    }

    /**
     * 获取定长的随机数，只包含小写字母
     *
     * @param length 随机数长度
     * @return 随机字符串
     */
    public static String randomLowerString(int length) {
        return randomCharString(length).toLowerCase();
    }

    /**
     * 获取定长的随机数,只包含大写字母
     *
     * @param length 随机数长度
     * @return 随机字符串
     */
    public static String randomUpperString(int length) {
        return randomCharString(length).toUpperCase();
    }
}
