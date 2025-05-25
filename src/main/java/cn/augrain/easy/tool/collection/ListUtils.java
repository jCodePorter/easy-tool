package cn.augrain.easy.tool.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * ListUtils
 *
 * @author biaoy
 * @since 2025/05/25
 */
public class ListUtils {

    public static <T> List<T> newArrayList() {
        return new ArrayList<>();
    }

    public static <T> boolean isEmpty(final List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(final List<T> list) {
        return !isEmpty(list);
    }

    public static <T> boolean isEmpty(final Collection<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(final Collection<T> list) {
        return !isEmpty(list);
    }

}
