package com.gk.study.utils;

import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * JSON工具类
 *
 * 该工具类提供了JSON序列化和反序列化的功能。
 * 主要功能包括：
 * 1. 对象转JSON字符串
 * 2. JSON字符串转对象
 * 3. JSON字符串转List
 * 4. 字节数组转对象
 *
 * 使用说明：
 * 1. 使用Jackson作为JSON处理库
 * 2. 支持泛型对象的转换
 * 3. 支持空值和异常处理
 *
 * 注意：
 * 1. 对象需要有无参构造函数
 * 2. 需要处理序列化异常
 * 3. 空值会返回null或空集合
 *
 * @author Administrator
 * @version 1.0
 * @date 2024-03-26
 */
public class JsonUtils {
    
    /**
     * Jackson对象映射器
     * 用于JSON的序列化和反序列化
     */
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 对象转JSON字符串
     *
     * 将Java对象转换为JSON格式的字符串。
     * 支持所有可序列化的Java对象。
     *
     * @param object 要转换的Java对象
     * @return String JSON格式的字符串
     * @throws RuntimeException 当序列化失败时抛出
     */
    public static String toJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON字符串转对象
     *
     * 将JSON格式的字符串转换为指定类型的Java对象。
     * 如果输入字符串为空，返回null。
     *
     * @param text JSON格式的字符串
     * @param clazz 目标对象的Class
     * @param <T> 目标对象的类型
     * @return T 转换后的Java对象
     * @throws RuntimeException 当反序列化失败时抛出
     */
    public static <T> T parseObject(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        try {
            return objectMapper.readValue(text, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 字节数组转对象
     *
     * 将JSON格式的字节数组转换为指定类型的Java对象。
     * 如果输入字节数组为空，返回null。
     *
     * @param bytes JSON格式的字节数组
     * @param clazz 目标对象的Class
     * @param <T> 目标对象的类型
     * @return T 转换后的Java对象
     * @throws RuntimeException 当反序列化失败时抛出
     */
    public static <T> T parseObject(byte[] bytes, Class<T> clazz) {
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        try {
            return objectMapper.readValue(bytes, clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON字符串转泛型对象
     *
     * 将JSON格式的字符串转换为指定泛型类型的Java对象。
     * 用于处理复杂的泛型对象，如Map<String, List<T>>。
     *
     * @param text JSON格式的字符串
     * @param typeReference 目标对象的类型引用
     * @param <T> 目标对象的类型
     * @return T 转换后的Java对象
     * @throws RuntimeException 当反序列化失败时抛出
     */
    public static <T> T parseObject(String text, TypeReference<T> typeReference) {
        try {
            return objectMapper.readValue(text, typeReference);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * JSON字符串转List
     *
     * 将JSON格式的字符串转换为指定类型的List。
     * 如果输入字符串为空，返回空List。
     *
     * @param text JSON格式的字符串
     * @param clazz List中元素的Class
     * @param <T> List中元素的类型
     * @return List<T> 转换后的List对象
     * @throws RuntimeException 当反序列化失败时抛出
     */
    public static <T> List<T> parseArray(String text, Class<T> clazz) {
        if (StringUtils.isEmpty(text)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(text, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}


