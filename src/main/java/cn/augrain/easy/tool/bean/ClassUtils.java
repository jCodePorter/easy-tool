package cn.augrain.easy.tool.bean;

import cn.augrain.easy.tool.consts.StrConst;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * ClassUtils
 *
 * @author biaoy
 * @since 2025/05/24
 */
public class ClassUtils {

    private static final String CLASS_PREFIX = "class ";

    private ClassUtils() {

    }

    /**
     * 反射创建对象，支持创建内部类
     */
    public static <T> T newInstance(Class<T> type) {
        try {
            boolean isMemberClass = type.isMemberClass();
            if (!isMemberClass) {
                return type.newInstance();
            }
            List<Class<?>> cs = new ArrayList<>();
            cs.add(type);
            Class<?> cc = type;
            while (cc.isMemberClass()) {
                cs.add(0, cc.getEnclosingClass());
                cc = cc.getEnclosingClass();
            }

            Object current = null;
            for (Class<?> c : cs) {
                if (current == null) {
                    current = c.getDeclaredConstructor().newInstance();
                } else {
                    current = c.getDeclaredConstructor(current.getClass()).newInstance(current);
                }
            }
            return (T) current;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 判断是不是接口
     *
     * @param type
     * @return
     */
    public static boolean isInterface(Type type) {
        return type.toString().indexOf("interface ") == 0;
    }

    /**
     * 判断类是不是一个class
     *
     * @param type
     * @return
     */
    public static boolean isClass(Type type) {
        return type.toString().indexOf(CLASS_PREFIX) == 0;
    }

    /**
     * 判断是不是基本类型
     *
     * @param type
     * @return
     */
    public static boolean isPrimitive(Type type) {
        return type.toString().equals("int") || type.toString().equals("double") || type.toString().equals("short")
                || type.toString().equals("long") || type.toString().equals("float") || type.toString().equals("boolean")
                || type.toString().equals("char") || type.toString().equals("byte");
    }

    /**
     * 判断是否为泛型
     *
     * @param type
     * @return
     */
    public static boolean isGenericType(Type type) {
        return type.toString().indexOf(StrConst.STR_LEFT_ANGLE_BRACKET) > 0;
    }

    /**
     * 加载类 不能传数组类，基本类型
     *
     * @param className 类名
     */
    public static Class forName(String className) {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据字段名从目标类中，获取Field
     *
     * @param idName 字段名
     * @param clazz  目标类
     * @return
     * @throws NoSuchFieldException 没有该字段名，会抛出异常
     */
    public static Field getField(String idName, Class<?> clazz) throws NoSuchFieldException {
        Field field;
        try {
            field = clazz.getDeclaredField(idName);
        } catch (NoSuchFieldException e) {
            field = clazz.getSuperclass().getDeclaredField(idName);
        }
        return field;
    }
}
