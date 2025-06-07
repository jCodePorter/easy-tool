package cn.augrain.easy.tool.stream;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Stream 工具类
 *
 * @author biaoy
 * @since 2025/06/07
 */
public class StreamUtils {

    /**
     * 将集合转换为映射
     *
     * @param list      集合
     * @param keyMapper 键映射函数
     * @param <T>       元素类型
     * @param <K>       键类型
     * @return 映射
     */
    public static <T, K> Map<K, T> toMap(List<T> list, Function<T, K> keyMapper) {
        return toMap(list, keyMapper, Function.identity());
    }

    /**
     * 将集合转换为映射
     *
     * @param list        集合
     * @param keyMapper   键映射函数
     * @param valueMapper 值映射函数
     * @param <T>         元素类型
     * @param <K>         键类型
     * @param <V>         值类型
     * @return 映射
     */
    public static <T, K, V> Map<K, V> toMap(List<T> list, Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return list.stream().collect(Collectors.toMap(keyMapper, valueMapper));
    }

    /**
     * 将集合分组
     *
     * @param list       集合
     * @param classifier 分类函数
     * @param <T>        元素类型
     * @param <K>        键类型
     * @return 分组映射
     */
    public static <T, K> Map<K, List<T>> groupBy(List<T> list, Function<T, K> classifier) {
        return list.stream().collect(Collectors.groupingBy(classifier));
    }

    /**
     * 将集合分组并指定下游收集器
     *
     * @param list       集合
     * @param classifier 分类函数
     * @param downstream 下游收集器
     * @param <T>        元素类型
     * @param <K>        键类型
     * @param <A>        下游收集器的中间类型
     * @param <D>        下游收集器的结果类型
     * @return 分组映射
     */
    public static <T, K, A, D> Map<K, D> groupBy(List<T> list, Function<T, K> classifier,
                                                 Collector<? super T, A, D> downstream) {
        return list.stream().collect(Collectors.groupingBy(classifier, downstream));
    }

    /**
     * 过滤集合
     *
     * @param list      集合
     * @param predicate 断言
     * @param <T>       元素类型
     * @return 过滤后的列表
     */
    public static <T> List<T> filter(List<T> list, Predicate<T> predicate) {
        return list.stream().filter(predicate).collect(Collectors.toList());
    }

    /**
     * 映射集合
     *
     * @param list   集合
     * @param mapper 映射函数
     * @param <T>    原始类型
     * @param <R>    目标类型
     * @return 映射后的列表
     */
    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        return list.stream().map(mapper).collect(Collectors.toList());
    }

    /**
     * 扁平化映射集合
     *
     * @param list   集合
     * @param mapper 映射函数
     * @param <T>    原始类型
     * @param <R>    目标类型
     * @return 扁平化映射后的列表
     */
    public static <T, R> List<R> flatMap(List<T> list, Function<T, List<R>> mapper) {
        return list.stream().flatMap(e -> mapper.apply(e).stream()).collect(Collectors.toList());
    }

    /**
     * 去重
     *
     * @param list 集合
     * @param <T>  元素类型
     * @return 去重后的列表
     */
    public static <T> List<T> distinct(List<T> list) {
        return list.stream().distinct().collect(Collectors.toList());
    }

    /**
     * 排序
     *
     * @param list 集合
     * @param <T>  元素类型
     * @return 排序后的列表
     */
    public static <T extends Comparable<T>> List<T> sorted(List<T> list) {
        return list.stream().sorted().collect(Collectors.toList());
    }
}