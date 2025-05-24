package cn.augrain.easy.tool.numeric;


import cn.augrain.easy.tool.core.ObjectUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Objects;

/**
 * {@link BigDecimal}工具类，
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class BigDecimalUtils {

    public static final BigDecimal HUNDRED = new BigDecimal("100");
    public static final BigDecimal THOUSAND = new BigDecimal("1000");

    private BigDecimalUtils() {
    }

    /**
     * 多个BigDecimal数据相乘
     */
    public static BigDecimal multiply(BigDecimal from, BigDecimal... to) {
        BigDecimal result = safe(from);
        if (to != null) {
            for (BigDecimal t : to) {
                result = result.multiply(safe(t));
            }
        }
        return result;
    }

    /**
     * 多个字符串数据相乘
     */
    public static BigDecimal multiply(String from, String... to) {
        BigDecimal result = strToDecimalDefaultZero(from);
        if (to != null) {
            for (String t : to) {
                result = result.multiply(safe(strToDecimalDefaultZero(t)));
            }
        }
        return result;
    }

    /**
     * 多个BigDecimal数据相加
     */
    public static BigDecimal add(BigDecimal from, BigDecimal... to) {
        BigDecimal result = safe(from);
        if (to != null) {
            for (BigDecimal t : to) {
                result = result.add(safe(t));
            }
        }
        return result;
    }

    /**
     * 多个字符串相加
     */
    public static BigDecimal add(String from, String... to) {
        BigDecimal result = strToDecimalDefaultZero(from);
        if (to != null) {
            for (String t : to) {
                result = add(result, t);
            }
        }
        return result;
    }

    /**
     * 第一个为BigDecimal 第二个为字符串
     */
    public static BigDecimal add(BigDecimal from, String to) {
        BigDecimal result = from;
        if (to != null && !to.isEmpty()) {
            result = result.add(strToDecimalDefaultZero(to));
        }
        return result;
    }


    /**
     * 减法 多个减数
     */
    public static BigDecimal subtract(BigDecimal from, BigDecimal... to) {
        BigDecimal result = safe(from);
        if (to != null) {
            for (BigDecimal t : to) {
                result = result.subtract(safe(t));
            }
        }
        return result;
    }

    /**
     * 除法 保留两位小数 除数为0 则为0
     */
    public static BigDecimal divide2(BigDecimal from, BigDecimal to) {
        BigDecimal result = safe(from);
        if (to == null) {
            return result;
        } else if (to.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return result.divide(to, 2, RoundingMode.HALF_UP);
        }
    }

    /**
     * BigDecimal除法 然后设置scale 除数为0，为空都结果为null
     */
    public static BigDecimal divide3(BigDecimal from, BigDecimal to) {
        BigDecimal result = safe(from);
        if (to == null || to.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        } else {
            return result.divide(to, 2, RoundingMode.HALF_UP);
        }

    }

    /**
     * BigDecimal除法 然后设置scale 除数为0，为空都结果为0
     */
    public static BigDecimal divide(BigDecimal from, BigDecimal to, int scale) {
        BigDecimal result = safe(from);
        if (to == null) {
            return result;
        } else if (to.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        } else {
            return result.divide(to, scale, RoundingMode.HALF_UP);
        }
    }

    /**
     * 字符串除法 然后设置scale
     */
    public static BigDecimal divide(String from, String to, int scale) {
        BigDecimal decimalFrom = strToDecimalDefaultNull(from);
        BigDecimal decimalTo = strToDecimalDefaultNull(to);
        return divide(decimalFrom, decimalTo, scale);
    }

    /**
     * 计算百分比
     */
    public static BigDecimal dividePercent(BigDecimal from, BigDecimal to) {
        if (from == null || to == null) {
            return null;
        } else {
            from = multiply(from, HUNDRED);
            return divide(from, to, 4);
        }
    }

    /**
     * 千分级 小数转千分比
     */
    public static BigDecimal multiplyThousand(BigDecimal baseVal) {
        BigDecimal safe = safe(baseVal);
        return multiply(safe, THOUSAND);
    }

    /**
     * 千分级 转成千分小数
     */
    public static BigDecimal divideThousand(BigDecimal baseVal) {
        BigDecimal safe = safe(baseVal);
        return divide(safe, THOUSAND, 4);
    }

    /**
     * 百分比转成小数
     */
    public static BigDecimal divideHundred(BigDecimal baseVal) {
        BigDecimal safe = safe(baseVal);
        return divide(safe, HUNDRED, 4);
    }

    /**
     * 小数转成百分比的值
     */
    public static BigDecimal multiplyHundred(BigDecimal baseVal) {
        BigDecimal safe = safe(baseVal);
        return multiply(safe, HUNDRED);
    }

    /**
     * 比较两个BigDecimal数据大小 返回int
     */
    public static int compareTo(BigDecimal from, BigDecimal to) {
        return safe(from).compareTo(safe(to));
    }

    /**
     * 比较两个BigDecimal数据大小 返回true false
     */
    public static boolean greaterThan(BigDecimal from, BigDecimal to) {
        return safe(from).compareTo(safe(to)) > 0;
    }

    /**
     * 比较两个字符串数据比较
     */
    public static int compareTo(String from, String to) {
        BigDecimal decimalFrom = strToDecimalDefaultZero(from);
        BigDecimal decimalTo = strToDecimalDefaultZero(to);
        return safe(decimalFrom).compareTo(safe(decimalTo));
    }

    /**
     * 数据为空则返回Zero
     */
    private static BigDecimal safe(BigDecimal target) {
        if (Objects.isNull(target)) {
            return BigDecimal.ZERO;
        } else {
            return target;
        }
    }

    /**
     * 字符串转BigDecimal 为空则返回Zero
     */
    public static BigDecimal strToDecimalDefaultZero(String s) {
        return strToDecimal(s, BigDecimal.ZERO);
    }

    /**
     * 字符串转BigDecimal 为空则返回空
     */
    public static BigDecimal strToDecimalDefaultNull(String s) {
        return strToDecimal(s, null);
    }

    /**
     * 字符串转BigDecimal 为空默认值defaultV
     */
    private static BigDecimal strToDecimal(String s, BigDecimal defaultV) {
        if (ObjectUtils.isEmpty(s)) {
            return defaultV;
        }
        return new BigDecimal(s);
    }

    /**
     * BigDecimal值如果为空转null
     */
    public static BigDecimal decimalDefaultNull(BigDecimal val) {
        if (ObjectUtils.isEmpty(val)) {
            return null;
        }
        return val;
    }

    /**
     * BigDecimal值如果为空转BigDecimal-ZERO
     */
    public static BigDecimal decimalDefaultZero(BigDecimal val) {
        return ObjectUtils.isEmpty(val) ? BigDecimal.ZERO : val;
    }

    /**
     * 根据pattern格式化decimal数据
     */
    public static String toString(BigDecimal decimal, String pattern) {
        DecimalFormat decimalFormat = new DecimalFormat(pattern);
        decimalFormat.setGroupingUsed(false);
        return decimal == null ? null : decimalFormat.format(decimal);
    }

    /**
     * 设置小数位数,四舍五入
     */
    public static BigDecimal setScale(BigDecimal bigDecimal, int scale) {
        if (bigDecimal == null) {
            return null;
        }
        return bigDecimal.divide(new BigDecimal(1), scale, RoundingMode.HALF_UP);
    }

    /**
     * 减法 减去多个值
     */
    public static BigDecimal subtract(String from, String... to) {
        BigDecimal result = strToDecimalDefaultZero(from);
        if (to != null) {
            for (String t : to) {
                result = result.subtract(strToDecimalDefaultZero(t));
            }
        }
        return result;
    }

    /**
     * 字符串数value 据转成BigDecimal 默认值defaultV
     */
    private static BigDecimal strToDecimalToDefaultV(String value, BigDecimal defaultV, int scale) {
        if (ObjectUtils.isEmpty(value)) {
            return defaultV;
        }
        try {
            return new BigDecimal(value).setScale(scale, RoundingMode.HALF_UP);
        } catch (Exception e) {
            return defaultV;
        }
    }

    /**
     * double-BigDecimal scale位小数
     */
    public static BigDecimal doubleValueSetScale(double value, int scale) {
        return setScale(new BigDecimal(value), scale);
    }

}
