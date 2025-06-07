package cn.augrain.easy.tool.lang;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * ObjectUtils
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class ObjectUtils {

    /**
     * 判断对象是否为空
     *
     * @param obj 对象
     * @return true/false
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        } else if (obj instanceof Optional) {
            Optional<?> optional = (Optional) obj;
            return optional.isPresent();
        } else if (obj instanceof CharSequence) {
            CharSequence charSequence = (CharSequence) obj;
            return StringUtils.isEmpty(charSequence);
        } else if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        } else if (obj instanceof Collection) {
            Collection<?> collection = (Collection) obj;
            return collection.isEmpty();
        } else if (obj instanceof Map) {
            Map<?, ?> map = (Map) obj;
            return map.isEmpty();
        } else {
            return false;
        }
    }
}
