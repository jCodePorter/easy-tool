package cn.augrain.easy.tool.collection;

import java.util.ArrayList;
import java.util.List;

/**
 * ListUtils
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class ListUtils {

    /**
     * 创建新集合
     */
    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    /**
     * 条件成立再添加
     *
     * @param condition condition条件
     * @param list      目标集合
     * @param t         待添加的原型
     * @param <T>       泛型
     */
    public static <T> void addIf(boolean condition, List<T> list, T t) {
        if (condition) {
            list.add(t);
        }
    }

    /**
     * 列表分批
     */
    public static <T> List<List<T>> partition(List<T> list, int batchSize) {
        List<List<T>> batches = new ArrayList<>();
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(list.size(), i + batchSize);
            batches.add(list.subList(i, end));
        }
        return batches;
    }

}
