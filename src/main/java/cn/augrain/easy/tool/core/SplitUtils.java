package cn.augrain.easy.tool.core;

import cn.augrain.easy.tool.consts.StrConst;
import cn.augrain.easy.tool.convert.TypeConverter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 拆分工具
 *
 * @author biaoy
 * @since 2025/06/21
 */
public class SplitUtils {

    /**
     * 字符串拆分为数组
     *
     * @param str   输入字符串
     * @param regex 正则
     * @return 数组
     */
    public static String[] splitToArray(String str, String regex) {
        if (StringUtils.isEmpty(str)) {
            return new String[0];
        }
        return str.split(regex);
    }

    public static <T> T[] splitToArray(String str, String regex, Class<T> clazz) {
        if (StringUtils.isEmpty(str)) {
            return (T[]) new Object[0];
        }

        String[] split = str.split(regex);
        T[] arr = (T[]) java.lang.reflect.Array.newInstance(clazz, split.length);
        for (int i = 0; i < split.length; i++) {
            arr[i] = TypeConverter.convert(split[i], clazz);
        }
        return arr;
    }

    /**
     * 切分字符串(分隔符默认逗号)
     *
     * @param str 被切分的字符串
     * @return 分割后的数据列表
     */
    public static List<String> splitToString(String str) {
        return splitTo(str, String::valueOf);
    }

    /**
     * 切分字符串(分隔符默认逗号)
     *
     * @param str 被切分的字符串
     * @return 分割后的数据列表
     */
    public static List<Long> splitToLong(String str) {
        return splitTo(str, s -> Long.parseLong(s.toString()));
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @return 分割后的数据列表
     */
    public static List<String> splitToString(String str, String separator) {
        return splitTo(str, separator, String::valueOf);
    }

    /**
     * 切分字符串自定义转换(分隔符默认逗号)
     *
     * @param str    被切分的字符串
     * @param mapper 自定义转换
     * @return 分割后的数据列表
     */
    public static <T> List<T> splitTo(String str, Function<? super Object, T> mapper) {
        return splitTo(str, StrConst.STR_COMMA, mapper);
    }

    /**
     * 切分字符串自定义转换
     *
     * @param str       被切分的字符串
     * @param separator 分隔符
     * @param mapper    自定义转换
     * @return 分割后的数据列表
     */
    public static <T> List<T> splitTo(String str, String separator, Function<? super Object, T> mapper) {
        if (StringUtils.isBlank(str)) {
            return new ArrayList<>(0);
        }
        return Arrays.stream(str.split(separator))
                .filter(Objects::nonNull)
                .map(mapper)
                .collect(Collectors.toList());
    }
}
