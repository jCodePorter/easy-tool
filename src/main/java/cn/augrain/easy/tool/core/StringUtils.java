package cn.augrain.easy.tool.core;

/**
 * 字符串
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class StringUtils {

    /**
     * 判断字符串是否为 null 或空
     */
    public static boolean isEmpty(CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 判断字符串是否为非 null 且非空
     */
    public static boolean isNotEmpty(CharSequence str) {
        return !isEmpty(str);
    }

    /**
     * 判断字符串是否为 null 或空白字符
     */
    public static boolean isBlank(CharSequence str) {
        if (isEmpty(str)) {
            return true;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串是否为非 null 且非空白字符
     */
    public static boolean isNotBlank(CharSequence str) {
        return !isBlank(str);
    }

    /**
     * 截取指定长度的字符串，超出部分用省略号表示
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        if (str.length() <= maxLength) {
            return str;
        }
        return str.substring(0, maxLength) + "...";
    }

    /**
     * 截取两个字符串之间的内容
     */
    public static String substringBetween(String str, String open, String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * 移除字符串两端的空白字符
     */
    public static String trim(String str) {
        return str == null ? null : str.trim();
    }

    /**
     * 移除字符串中的所有空白字符
     */
    public static String removeAllWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.replaceAll("\\s+", "");
    }

    /**
     * 判断字符串是否以指定前缀开头（忽略大小写）
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        return str.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * 判断字符串是否以指定后缀结尾（忽略大小写）
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.length() < suffix.length()) {
            return false;
        }
        return str.regionMatches(true, str.length() - suffix.length(), suffix, 0, suffix.length());
    }

    /**
     * 判断字符串是否为数字
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 将字符串首字母大写
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

    /**
     * 将字符串首字母小写
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) {
            return str;
        }
        return str.substring(0, 1).toLowerCase() + str.substring(1);
    }

    /**
     * 重复字符串指定次数
     */
    public static String repeat(CharSequence str, int repeat) {
        if (str == null) {
            return null;
        }
        if (repeat <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < repeat; i++) {
            sb.append(str);
        }
        return sb.toString();
    }

    /**
     * 安全转换为字符串（避免null转为"null"字符串）
     */
    public static String toString(Object obj) {
        return obj == null ? null : obj.toString();
    }

    /**
     * 字符串编码
     *
     * @param str 输入待编码的字符串
     * @return 编码后字符串
     */
    public static String escape(String str) {
        int i;
        char j;
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(str.length() * 6);
        for (i = 0; i < str.length(); i++) {
            j = str.charAt(i);
            if (Character.isDigit(j) || Character.isLowerCase(j) || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%");
                if (j < 16) {
                    tmp.append("0");
                }
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u");
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    /**
     * 字符串解码
     *
     * @param str 输入字符串
     * @return 解码字符串
     */
    public static String unescape(String str) {
        StringBuilder tmp = new StringBuilder();
        tmp.ensureCapacity(str.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < str.length()) {
            pos = str.indexOf("%", lastPos);
            if (pos == lastPos) {
                if (str.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(str.substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(str.substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(str.substring(lastPos));
                    lastPos = str.length();
                } else {
                    tmp.append(str, lastPos, pos);
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
}
