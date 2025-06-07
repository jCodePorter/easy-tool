package cn.augrain.easy.tool.util;

import cn.augrain.easy.tool.exception.UtilsRuntimeException;
import cn.augrain.easy.tool.lang.StringUtils;

import java.util.Objects;

/**
 * 断言工具类
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class AssertUtils {

    private AssertUtils() {

    }

    /**
     * @param condition 条件
     * @param detail    错误详细信息
     */
    public static void assertTrue(boolean condition, String detail) {
        if (!condition) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param condition 条件
     * @param detail    错误详细信息
     */
    public static void assertFalse(boolean condition, String detail) {
        if (condition) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param obj    对象
     * @param detail 错误详细信息
     */
    public static void assertNotNull(Object obj, String detail) {
        if (Objects.isNull(obj)) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param str    目标字符串
     * @param detail 错误详细信息
     */
    public static void assertNotBlank(String str, String detail) {
        if (StringUtils.isBlank(str)) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param str    目标字符串
     * @param detail 错误详细信息
     */
    public static void assertBlank(String str, String detail) {
        if (StringUtils.isNotBlank(str)) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param obj    目标对象
     * @param detail 错误详细信息
     */
    public static void assertNull(Object obj, String detail) {
        if (Objects.nonNull(obj)) {
            throw new UtilsRuntimeException(detail);
        }
    }

    /**
     * @param condition 条件
     * @param detail    错误详细信息
     */
    public static void assertTrue(boolean condition, int code, String detail) {
        if (!condition) {
            throw new UtilsRuntimeException(code, detail);
        }
    }

    /**
     * @param condition 条件
     * @param detail    错误详细信息
     */
    public static void assertFalse(boolean condition, int code, String detail) {
        if (condition) {
            throw new UtilsRuntimeException(code, detail);
        }
    }

    /**
     * @param obj    目标对象
     * @param code   状态码
     * @param detail 错误详细信息
     */
    public static void assertNotNull(Object obj, int code, String detail) {
        if (Objects.isNull(obj)) {
            throw new UtilsRuntimeException(code, detail);
        }
    }

    /**
     * @param str    目标字符串
     * @param code   状态码
     * @param detail 错误详细信息
     */
    public static void assertNotBlank(String str, int code, String detail) {
        if (StringUtils.isBlank(str)) {
            throw new UtilsRuntimeException(code, detail);
        }
    }

    /**
     * @param obj    目标对象
     * @param code   状态码
     * @param detail 错误详细信息
     */
    public static void assertNull(Object obj, int code, String detail) {
        if (Objects.nonNull(obj)) {
            throw new UtilsRuntimeException(code, detail);
        }
    }

}
