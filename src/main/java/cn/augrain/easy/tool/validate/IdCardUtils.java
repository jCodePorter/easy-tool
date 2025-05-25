package cn.augrain.easy.tool.validate;

/**
 * 身份证工具类
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class IdCardUtils {

    private IdCardUtils() {
    }

    /**
     * 加权因子
     */
    private static final int[] W = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 18位二代身份证号码的正则表达式
     */
    private static final String REGEX_ID_NO_18 = "^"
            + "\\d{6}" // 6位地区码
            + "(18|19|([23]\\d))\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}" // 3位顺序码
            + "[0-9Xx]" // 校验码
            + "$";

    /**
     * 15位一代身份证号码的正则表达式
     */
    private static final String REGEX_ID_NO_15 = "^"
            + "\\d{6}" // 6位地区码
            + "\\d{2}" // 年YYYY
            + "((0[1-9])|(10|11|12))" // 月MM
            + "(([0-2][1-9])|10|20|30|31)" // 日DD
            + "\\d{3}"// 3位顺序码
            + "$";


    /**
     * 校验身份证号码
     * 适用于18位的二代身份证号码
     *
     * @param idNo 身份证号码
     * @return true - 校验通过<br>
     * false - 校验不通过
     * @throws IllegalArgumentException 如果身份证号码为空或长度不为18位或不满足身份证号码组成规则
     *                                  6位地址码+出生年月日YYYYMMDD+3位顺序码+0~9或X(x)校验码</i>
     */
    public static boolean checkIdNo(String idNo) {
        // 校验身份证号码的长度
        if (!checkStrLength(idNo, 18)) {
            return false;
        }
        // 匹配身份证号码的正则表达式
        if (!regexMatch(idNo, REGEX_ID_NO_18)) {
            return false;
        }
        // 校验身份证号码的验证码
        return validateCheckNumber(idNo);
    }

    /**
     * 校验字符串长度
     *
     * @param inputString 字符串
     * @param len         预期长度
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean checkStrLength(String inputString, int len) {
        return inputString != null && inputString.length() == len;
    }

    /**
     * 匹配正则表达式
     *
     * @param inputString 字符串
     * @param regex       正则表达式
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean regexMatch(String inputString, String regex) {
        return inputString.matches(regex);
    }

    /**
     * 校验码校验
     * <p>
     * 适用于18位的二代身份证号码
     * </p>
     *
     * @param idNo 身份证号码
     * @return true - 校验通过<br>
     * false - 校验不通过
     */
    private static boolean validateCheckNumber(String idNo) {
        char[] IDNoArray = idNo.toCharArray();
        int sum = 0;
        for (int i = 0; i < W.length; i++) {
            sum += Integer.parseInt(String.valueOf(IDNoArray[i])) * W[i];
        }
        // 校验位是X，则表示10
        if (IDNoArray[17] == 'X' || IDNoArray[17] == 'x') {
            sum += 10;
        } else {
            sum += Integer.parseInt(String.valueOf(IDNoArray[17]));
        }
        // 如果除11模1，则校验通过
        return sum % 11 == 1;
    }

    /**
     * 15位一代身份证号码升级18位二代身份证号码
     * 为15位的一代身份证号码增加年份的前2位和最后1位校验码
     *
     * @param idNo15 15位的一代身份证号码
     * @return 18位的二代身份证号码
     */
    public static String updateIdNo15to18(String idNo15) {
        // 校验身份证号码的长度
        if (!checkStrLength(idNo15, 15)) {
            throw new IllegalArgumentException();
        }
        // 匹配身份证号码的正则表达式
        if (!regexMatch(idNo15, REGEX_ID_NO_15)) {
            throw new IllegalArgumentException();
        }
        // 得到本体码，因一代身份证皆为19XX年生人，年份中增加19，组成4位
        String masterNumber = idNo15.substring(0, 6) + "19" + idNo15.substring(6);
        // 计算校验码
        String checkNumber = computeCheckNumber(masterNumber);
        // 返回本体码+校验码=完整的身份证号码
        return masterNumber + checkNumber;
    }

    /**
     * 计算校验码
     * 适用于18位的二代身份证号码
     *
     * @param masterNumber 本体码
     * @return 校验码
     */
    private static String computeCheckNumber(String masterNumber) {
        char[] masterNumberArray = masterNumber.toCharArray();
        int sum = 0;
        for (int i = 0; i < W.length; i++) {
            sum += Integer.parseInt(String.valueOf(masterNumberArray[i])) * W[i];
        }
        // 根据同余定理得到的校验码数组
        String[] checkNumberArray = {"1", "0", "X", "9", "8", "7", "6", "5", "4", "3", "2"};
        // 返回校验码
        return checkNumberArray[sum % 11];
    }
}
