package cn.augrain.easy.tool.bean;


import cn.augrain.easy.tool.lang.StringUtils;
import cn.augrain.easy.tool.time.LocalDateTimeUtils;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;


/**
 * JSON 转换工具
 *
 * @create: 2019-09-02 19:44
 */
@Slf4j
public class JSONUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // 忽略未知属性，而不返回失败
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true); // 允许非标准的json格式：json字段名称可以不使用"包裹
        OBJECT_MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // 允许非标准的json格式：json字段名称和字段值可以使用'包裹
        OBJECT_MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        OBJECT_MAPPER.registerModule(new SimpleModule()
                .addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                    @Override
                    public LocalDateTime deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
                        return LocalDateTimeUtils.parse(jsonParser.getValueAsString());
                    }
                })
                .addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                    @Override
                    public void serialize(
                            LocalDateTime localDateTime, JsonGenerator jsonGenerator, SerializerProvider serializerProvider)
                            throws IOException {
                        jsonGenerator.writeString(LocalDateTimeUtils.format(localDateTime, LocalDateTimeUtils.NORM_DATETIME_PATTERN));
                    }
                })
        );
    }

    /**
     * Object可以是POJO，也可以是Collection或数组。
     * 如果对象为Null, 返回"null".
     * 如果集合为空集合, 返回"[]".
     */
    public static String toString(Object object) {
        try {
            return OBJECT_MAPPER.writeValueAsString(object);
        } catch (IOException e) {
            log.error("method fromJson happen err : {}, jsonString : {}", e, object);
            return null;
        }
    }

    /**
     * 反序列化POJO或简单Collection如List<String>.
     * <p>
     * 如果JSON字符串为Null或"null"字符串, 返回Null.
     * 如果JSON字符串为"[]", 返回空集合.
     * <p>
     * 如需反序列化复杂Collection如List<MyBean>, 请使用fromJson(String,JavaType)
     */
    public <T> T fromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) return null;
        try {
            return OBJECT_MAPPER.readValue(jsonString, clazz);
        } catch (Exception e) {
            log.error("method fromJson happen err : {}, jsonString : {}", e, jsonString);
            return null;
        }
    }

    public <T> T fromJson(InputStream inputStream, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(inputStream, clazz);
        } catch (IOException e) {
            log.error("method fromJson happen err", e);
            return null;
        }
    }

    /**
     * 解析集合
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    public <T> List<T> listFromJson(String jsonString, Class<T> clazz) {
        if (StringUtils.isEmpty(jsonString)) {
            return null;
        }
        JavaType javaType = OBJECT_MAPPER.getTypeFactory().constructParametricType(List.class, clazz);
        try {
            return OBJECT_MAPPER.readValue(jsonString, javaType);
        } catch (Exception e) {
            log.error("method fromJson happen err : {}, jsonString : {}", e, jsonString);
            return null;
        }
    }

    public ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }
}
