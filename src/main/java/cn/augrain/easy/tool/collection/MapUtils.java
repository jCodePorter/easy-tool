package cn.augrain.easy.tool.collection;

import cn.augrain.easy.tool.core.AssertUtils;

import java.util.HashMap;
import java.util.Map;

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

    public static Map<String, Object> of(Object... params) {
        AssertUtils.assertTrue(params.length % 2 == 0, "参数个数异常");

        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < params.length;) {
            map.put(String.valueOf(params[i]), params[i + 1]);
            i += 2;
        }
        return map;
    }
}
