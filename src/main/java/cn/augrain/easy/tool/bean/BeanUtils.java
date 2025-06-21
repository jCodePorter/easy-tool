package cn.augrain.easy.tool.bean;

import cn.augrain.easy.tool.collection.MapUtils;
import cn.augrain.easy.tool.core.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BeanUtils
 *
 * @author biaoy
 * @since 2025/05/24
 */
@Slf4j
public class BeanUtils {
    // 缓存BeanInfo信息
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> BEAN_INFO_CACHE =
            new ConcurrentHashMap<>();

    private BeanUtils() {

    }

    /**
     * 将bean转换为map
     *
     * @param obj 待转换的bean对象
     * @return map
     */
    public static Map<String, Object> toMap(Object obj) {
        if (obj == null) {
            return MapUtils.empty();
        }
        Map<String, Object> map = new HashMap<>();
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
            PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
            for (PropertyDescriptor property : propertyDescriptors) {
                String key = property.getName();
                if (!key.equals("class")) {
                    Method getter = property.getReadMethod();

                    Object value = getter.invoke(obj);
                    map.put(key, value);
                }
            }
        } catch (Exception e) {
            log.error("transform bean to map error ", e);
        }
        return map;
    }

    /**
     * 简单的复制出新类型对象.
     * <p>
     * 通过source.getClass() 获得源Class
     */
    public static <S, D> D copy(S source, Class<D> destinationClass) {
        D target = ClassUtils.newInstance(destinationClass);
        copyProperties(source, target, true);
        return target;
    }

    /**
     * 简单的复制出新对象列表到ArrayList
     */
    public static <T> List<T> copyList(List<?> sourceList, Class<T> destinationClass) {
        if (ObjectUtils.isEmpty(sourceList)) {
            return Collections.emptyList();
        }
        List<T> destinationList = new ArrayList<>();
        for (Object sourceObject : sourceList) {
            T destinationObject = copy(sourceObject, destinationClass);
            destinationList.add(destinationObject);
        }
        return destinationList;
    }

    /**
     * 复制属性
     *
     * @param source           源对象
     * @param target           目标对象
     * @param ignoreNull       是否忽略null值
     * @param ignoreProperties 忽略的属性名
     */
    public static void copyProperties(Object source, Object target, Boolean ignoreNull, String... ignoreProperties) {
        if (source == null || target == null) {
            return;
        }

        Set<String> ignoreSet = new HashSet<>(Arrays.asList(ignoreProperties));

        try {
            Map<String, PropertyDescriptor> sourceDescriptors = getCachedPropertyDescriptors(source.getClass());
            Map<String, PropertyDescriptor> targetDescriptors = getCachedPropertyDescriptors(target.getClass());

            for (PropertyDescriptor sourcePd : sourceDescriptors.values()) {
                String propertyName = sourcePd.getName();

                // 检查是否忽略该属性
                if (ignoreSet.contains(propertyName)) {
                    continue;
                }

                PropertyDescriptor targetPd = targetDescriptors.get(propertyName);
                if (targetPd != null) {
                    copyProperty(source, target, ignoreNull, sourcePd, targetPd);
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException("Failed to copy properties", e);
        }
    }

    /**
     * 复制单个属性
     */
    private static void copyProperty(Object source, Object target, Boolean ignoreNull,
                                     PropertyDescriptor sourcePd, PropertyDescriptor targetPd) {
        Method readMethod = sourcePd.getReadMethod();
        Method writeMethod = targetPd.getWriteMethod();

        if (readMethod != null && writeMethod != null) {
            try {
                // 检查类型兼容性
                if (isCompatibleType(sourcePd.getPropertyType(), targetPd.getPropertyType())) {
                    Object value = readMethod.invoke(source);
                    if (ignoreNull && value != null) {
                        writeMethod.invoke(target, value);
                    } else {
                        writeMethod.invoke(target, value);
                    }
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("happen exception ", e);
            }
        }
    }

    /**
     * 检查类型兼容性
     */
    private static boolean isCompatibleType(Class<?> sourceType, Class<?> targetType) {
        // 基本类型与包装类型兼容
        if (sourceType.isPrimitive()) {
            sourceType = wrapPrimitiveType(sourceType);
        }
        if (targetType.isPrimitive()) {
            targetType = wrapPrimitiveType(targetType);
        }
        return targetType.isAssignableFrom(sourceType);
    }

    /**
     * 基本类型转包装类型
     */
    private static Class<?> wrapPrimitiveType(Class<?> type) {
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == double.class) return Double.class;
        if (type == float.class) return Float.class;
        if (type == boolean.class) return Boolean.class;
        if (type == char.class) return Character.class;
        if (type == byte.class) return Byte.class;
        if (type == short.class) return Short.class;
        return type;
    }

    /**
     * 获取缓存的属性描述符
     */
    private static Map<String, PropertyDescriptor> getCachedPropertyDescriptors(Class<?> clazz)
            throws IntrospectionException {
        return BEAN_INFO_CACHE.computeIfAbsent(clazz, k -> {
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(k);
                PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();

                Map<String, PropertyDescriptor> descriptors = new HashMap<>();
                for (PropertyDescriptor pd : pds) {
                    if (!"class".equals(pd.getName())) {
                        descriptors.put(pd.getName(), pd);
                    }
                }
                return descriptors;
            } catch (IntrospectionException e) {
                throw new RuntimeException("Failed to introspect class: " + k.getName(), e);
            }
        });
    }
}
