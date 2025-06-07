package cn.augrain.easy.tool.collection;

import cn.augrain.easy.tool.util.AssertUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * MapUtils
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class MapUtils {

    private MapUtils() {

    }

    /**
     * 返回一个空map
     *
     * @return map
     */
    public static Map<String, Object> empty() {
        return new HashMap<>();
    }

    /**
     * 是否为空
     *
     * @param map map
     * @return true/false
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 是否不为空
     *
     * @param map map
     * @return true/false
     */
    public static boolean isNotEmpty(Map<?, ?> map) {
        return !isEmpty(map);
    }

    public static Map<String, Object> of(Object... params) {
        AssertUtils.assertTrue(params.length % 2 == 0, "参数个数异常");

        Map<String, Object> map = new HashMap<>(params.length / 2);
        for (int i = 0; i < params.length; ) {
            map.put(String.valueOf(params[i]), params[i + 1]);
            i += 2;
        }
        return map;
    }

    /**
     * toString，可以忽略某个字段
     *
     * @param map        输入map
     * @param ignoreKeys 忽略的key
     * @return string
     */
    public static String toString(Map<String, Object> map, Set<String> ignoreKeys) {
        return map.entrySet().stream()
                .filter(entry -> !ignoreKeys.contains(entry.getKey()))
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
