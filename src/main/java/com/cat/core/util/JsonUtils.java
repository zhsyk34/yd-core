package com.cat.core.util;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * @see com.fasterxml.jackson.annotation.JsonIgnore
 * @see com.fasterxml.jackson.annotation.JsonProperty
 * @see com.fasterxml.jackson.annotation.JsonInclude
 * @see com.fasterxml.jackson.annotation.JsonInclude.Include
 * @see com.fasterxml.jackson.annotation.JsonUnwrapped
 * @see com.fasterxml.jackson.annotation.JsonCreator
 * <p/>
 * @see com.fasterxml.jackson.databind.annotation.JsonNaming
 * @see com.fasterxml.jackson.databind.PropertyNamingStrategy
 * @see com.fasterxml.jackson.databind.annotation.JsonSerialize
 * @see com.fasterxml.jackson.databind.annotation.JsonDeserialize
 */
@SuppressWarnings({"WeakerAccess", "unused"})
@Slf4j
public abstract class JsonUtils {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final TypeFactory TYPE_FACTORY;

    static {
        initConfig();
        TYPE_FACTORY = mapper.getTypeFactory();
    }

    /**
     * @see com.fasterxml.jackson.core.JsonFactory.Feature
     */
    private static void initConfig() {
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.registerModule(new JavaTimeModule());

        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
    }

    public static String toJsonString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (Exception e) {
            error(e);
            return null;
        }
    }

    public static <T> T parseJson(String content, TypeReference<T> valueTypeRef) {
        try {
            return mapper.readValue(content, valueTypeRef);
        } catch (Exception e) {
            error(e);
            return null;
        }
    }

    public static <T> T parseJson(String content, Class<T> valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> T parseJson(String content, JavaType valueType) {
        try {
            return mapper.readValue(content, valueType);
        } catch (Exception e) {
            return null;
        }
    }

    public static <T> Collection<T> parseToCollection(String content, Class<T> valueType) {
//        return parseJson(content, new TypeReference<Collection<T>>() {
//        });
        //or
        return parseJson(content, TYPE_FACTORY.constructCollectionType(Collection.class, valueType));
    }

    public static <K, V> Map<K, V> parseToMap(String content, Class<K> keyType, Class<V> valueType) {
//        return parseJson(content, TYPE_FACTORY.constructMapLikeType(HashMap.class, keyType, valueType));
        return parseJson(content, new TypeReference<Map<K, V>>() {
        });
    }

    private static void error(Exception e) {
        logger.error(e.getMessage(), e);
    }

    private static void test1() {
        List<Integer> value = Arrays.asList(13, 15);
        String json = JsonUtils.toJsonString(value);
        System.out.println(json);

        Collection<Integer> list = parseToCollection(json, Integer.class);
        System.out.println(list);
    }

    private static void test2() {
        Map<Object, Object> map = new HashMap<>();
        map.put("1", 3);
        map.put(3, 4);

        String json = JsonUtils.toJsonString(map);
        System.out.println(json);

        Map<Long, String> r1 = parseToMap(json, Long.class, String.class);
        System.out.println(r1);

        Map<String, String> r2 = parseToMap(json, String.class, String.class);
        System.out.println(r2);

    }

    public static void main(String[] args) {
        test2();
    }

}
