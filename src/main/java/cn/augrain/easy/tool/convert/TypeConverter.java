package cn.augrain.easy.tool.convert;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换工具类
 *
 * @author biaoy
 * @since 2025/06/21
 */
public class TypeConverter {

    private static final Map<Type, Converter<?>> CONVERTERS = new HashMap<>();

    static {
        // 注册转换器
        CONVERTERS.put(String.class, new StringConverter());
        CONVERTERS.put(Integer.class, new IntegerConverter());
        CONVERTERS.put(int.class, CONVERTERS.get(Integer.class));
        CONVERTERS.put(Long.class, new LongConverter());
        CONVERTERS.put(long.class, CONVERTERS.get(Long.class));
        CONVERTERS.put(Double.class, new DoubleConverter());
        CONVERTERS.put(double.class, CONVERTERS.get(Double.class));
        CONVERTERS.put(Float.class, new FloatConverter());
        CONVERTERS.put(float.class, CONVERTERS.get(Float.class));
        CONVERTERS.put(Boolean.class, new BooleanConverter());
        CONVERTERS.put(boolean.class, CONVERTERS.get(Boolean.class));
        CONVERTERS.put(Short.class, new ShortConverter());
        CONVERTERS.put(short.class, CONVERTERS.get(Short.class));
        CONVERTERS.put(Byte.class, new ByteConverter());
        CONVERTERS.put(byte.class, CONVERTERS.get(Byte.class));
        CONVERTERS.put(Character.class, new CharacterConverter());
        CONVERTERS.put(char.class, CONVERTERS.get(Character.class));
        CONVERTERS.put(BigDecimal.class, new BigDecimalConverter());
        CONVERTERS.put(BigInteger.class, new BigIntegerConverter());
        CONVERTERS.put(Date.class, new DateConverter());
    }

    /**
     * 类型转换方法
     *
     * @param value      要转换的值
     * @param targetType 目标类型
     * @param <T>        泛型类型
     * @return 转换后的值
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(Object value, Class<T> targetType) {
        if (value == null) {
            return null;
        }

        // 如果已经是目标类型，直接返回
        if (targetType.isInstance(value)) {
            return (T) value;
        }

        Converter<?> converter = CONVERTERS.get(targetType);
        if (converter != null) {
            return (T) converter.convert(value);
        }

        throw new IllegalArgumentException("Unsupported target type: " + targetType);
    }

    /**
     * 类型转换方法（带默认值）
     *
     * @param value        要转换的值
     * @param targetType   目标类型
     * @param defaultValue 默认值
     * @param <T>          泛型类型
     * @return 转换后的值
     */
    public static <T> T convert(Object value, Class<T> targetType, T defaultValue) {
        try {
            T result = convert(value, targetType);
            return result != null ? result : defaultValue;
        } catch (Exception e) {
            return defaultValue;
        }
    }

    // 转换器接口
    private interface Converter<T> {
        T convert(Object value);
    }

    // 转换器实现
    private static class StringConverter implements Converter<String> {
        @Override
        public String convert(Object value) {
            return value.toString();
        }
    }

    private static class IntegerConverter implements Converter<Integer> {
        @Override
        public Integer convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).intValue();
            }
            return Integer.parseInt(value.toString());
        }
    }

    private static class LongConverter implements Converter<Long> {
        @Override
        public Long convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            return Long.parseLong(value.toString());
        }
    }

    private static class DoubleConverter implements Converter<Double> {
        @Override
        public Double convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).doubleValue();
            }
            return Double.parseDouble(value.toString());
        }
    }

    private static class FloatConverter implements Converter<Float> {
        @Override
        public Float convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).floatValue();
            }
            return Float.parseFloat(value.toString());
        }
    }

    private static class BooleanConverter implements Converter<Boolean> {
        @Override
        public Boolean convert(Object value) {
            if (value instanceof Boolean) {
                return (Boolean) value;
            }
            String str = value.toString().toLowerCase();
            return "true".equals(str) || "1".equals(str) || "yes".equals(str) || "on".equals(str);
        }
    }

    private static class ShortConverter implements Converter<Short> {
        @Override
        public Short convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).shortValue();
            }
            return Short.parseShort(value.toString());
        }
    }

    private static class ByteConverter implements Converter<Byte> {
        @Override
        public Byte convert(Object value) {
            if (value instanceof Number) {
                return ((Number) value).byteValue();
            }
            return Byte.parseByte(value.toString());
        }
    }

    private static class CharacterConverter implements Converter<Character> {
        @Override
        public Character convert(Object value) {
            String str = value.toString();
            if (str.length() != 1) {
                throw new IllegalArgumentException("Cannot convert to Character: " + value);
            }
            return str.charAt(0);
        }
    }

    private static class BigDecimalConverter implements Converter<BigDecimal> {
        @Override
        public BigDecimal convert(Object value) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value;
            }
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            }
            return new BigDecimal(value.toString().trim());
        }
    }

    private static class BigIntegerConverter implements Converter<BigInteger> {
        @Override
        public BigInteger convert(Object value) {
            if (value instanceof BigInteger) {
                return (BigInteger) value;
            }
            if (value instanceof Number) {
                return BigInteger.valueOf(((Number) value).longValue());
            }
            return new BigInteger(value.toString().trim());
        }
    }

    private static class DateConverter implements Converter<Date> {
        private static final String[] DATE_FORMATS = {
                "yyyy-MM-dd HH:mm:ss",
                "yyyy-MM-dd",
                "yyyy/MM/dd HH:mm:ss",
                "yyyy/MM/dd",
                "dd-MM-yyyy",
                "dd/MM/yyyy",
                "MM/dd/yyyy",
                "MM-dd-yyyy"
        };

        @Override
        public Date convert(Object value) {
            if (value instanceof Date) {
                return (Date) value;
            }
            if (value instanceof Number) {
                return new Date(((Number) value).longValue());
            }

            String str = value.toString().trim();
            for (String format : DATE_FORMATS) {
                try {
                    return new SimpleDateFormat(format).parse(str);
                } catch (ParseException ignored) {
                    // 尝试下一种格式
                }
            }
            throw new IllegalArgumentException("Cannot parse date: " + value);
        }
    }
}