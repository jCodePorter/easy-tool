package cn.augrain.easy.tool.collection;

import java.util.*;
import java.util.stream.Collectors;

/**
 * SetUtils
 *
 * @author biaoy
 * @since 2025/06/05
 */
public class SetUtils {

    /**
     * 通过字符串构建Set集合
     *
     * @param arg 输入参数
     * @return Set
     */
    public static Set<String> of(String... arg) {
        return Arrays.stream(arg).collect(Collectors.toSet());
    }

    /**
     * 计算多个Set的交集
     *
     * @param ignoreEmptyOrNull 是否忽略空集合或者null集合, 如果不忽略，只要存在一个空集合或者null集合即交集为空
     * @param sets              多个Set集合
     * @param <T>               泛型类型
     * @return 所有Set的交集，如果任一Set为null或空，返回空集合
     */
    public static <T> Set<T> intersection(boolean ignoreEmptyOrNull, Set<T>... sets) {
        if (sets == null || sets.length == 0) {
            return Collections.emptySet();
        }

        // 如果不忽略空集合
        if (!ignoreEmptyOrNull) {
            for (Set<T> set : sets) {
                if (set == null || set.isEmpty()) {
                    return Collections.emptySet();
                }
            }
        }

        return Arrays.stream(sets)
                .filter(Objects::nonNull)
                .filter(set -> !set.isEmpty())
                .reduce((set1, set2) -> {
                    set1.retainAll(set2);
                    return set1;
                })
                .orElse(Collections.emptySet());
    }

    /**
     * 计算多个Set的并集
     *
     * @param sets 多个Set集合
     * @param <T>  泛型类型
     * @return 所有Set的并集，如果所有Set都为null或空，返回空集合
     */
    @SafeVarargs
    public static <T> Set<T> union(Set<T>... sets) {
        if (sets == null || sets.length == 0) {
            return Collections.emptySet();
        }
        Set<T> result = new HashSet<>();
        for (Set<T> set : sets) {
            if (set != null) {
                result.addAll(set);
            }
        }
        return result;
    }

    /**
     * 计算多个Set的差集（第一个集合减去其他所有集合的元素）
     *
     * @param sets 多个Set集合
     * @param <T>  泛型类型
     * @return 第一个Set减去其他所有Set的元素，如果第一个Set为null或空，返回空集合
     */
    @SafeVarargs
    public static <T> Set<T> difference(Set<T>... sets) {
        if (sets == null || sets.length == 0) {
            return Collections.emptySet();
        }
        if (sets[0] == null || sets[0].isEmpty()) {
            return Collections.emptySet();
        }

        Set<T> result = new HashSet<>(sets[0]);
        // 从第一个集合中移除其他所有集合的元素
        for (int i = 1; i < sets.length; i++) {
            if (sets[i] != null) {
                result.removeAll(sets[i]);
            }
        }
        return result;
    }
}
