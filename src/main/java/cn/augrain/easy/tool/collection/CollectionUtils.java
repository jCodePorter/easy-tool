package cn.augrain.easy.tool.collection;

import java.util.Collection;

/**
 * 集合相关
 *
 * @since 2025/06/27
 */
public class CollectionUtils {

    public static <T> boolean isEmpty(final Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(final Collection<T> collection) {
        return !isEmpty(collection);
    }
}
