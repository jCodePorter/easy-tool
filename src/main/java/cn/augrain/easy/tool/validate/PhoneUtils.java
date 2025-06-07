package cn.augrain.easy.tool.validate;

import cn.augrain.easy.tool.lang.StringUtils;

/**
 * 手机号工具类
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class PhoneUtils {
    private PhoneUtils() {
    }

    /**
     * 手机号格式校验正则
     */
    private static final String PHONE_REGEX = "^1(3[0-9]|4[57]|5[0-35-9]|6[6]|7[0135678]|8[0-9]|9[89])\\d{8}$";

    /**
     * 手机号脱敏筛选正则
     */
    private static final String PHONE_BLUR_REGEX = "(\\d{3})\\d{4}(\\d{4})";

    /**
     * 手机号脱敏替换正则
     */
    private static final String PHONE_BLUR_REPLACE_REGEX = "$1****$2";

    /**
     * 手机号格式校验
     *
     * @param phone 手机号
     * @return true/false
     */
    public static boolean checkPhone(String phone) {
        if (StringUtils.isBlank(phone)) {
            return false;
        }
        return phone.matches(PHONE_REGEX);
    }

    /**
     * 手机号脱敏处理
     *
     * @param phone 手机号
     * @return 脱敏后的手机号
     */
    public static String blurPhone(String phone) {
        boolean checkFlag = checkPhone(phone);
        if (!checkFlag) {
            throw new IllegalArgumentException("手机号格式不正确!");
        }
        return phone.replaceAll(PHONE_BLUR_REGEX, PHONE_BLUR_REPLACE_REGEX);
    }
}
