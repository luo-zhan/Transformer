package com.robot.transform.component;

import lombok.experimental.UtilityClass;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * 字典接口
 * <p>
 * 自定义的字典枚举类实现本接口后可省略属性code和text，以及对应的get方法
 * 在构造方法中只需调用init方法即可初始化
 *
 * @author R
 * @date 2021-12
 */
public interface Dict<T extends Serializable> {
    /**
     * 通过code获取value
     *
     * @param clazz 枚举class
     * @param code  code
     * @return text
     */
    static <T extends Serializable> String getTextByCode(Class<? extends Dict<T>> clazz, T code) {
        return Stream.of(clazz.getEnumConstants())
                .filter((Dict<T> e) -> e.getCode().equals(code))
                .map(Dict::getText)
                .findAny().orElse(null);
    }

    /**
     * 通过text获取code
     *
     * @param clazz 枚举class
     * @param text  text
     * @return code
     */
    static <T extends Serializable> T getCodeByText(Class<? extends Dict<T>> clazz, String text) {
        return Stream.of(clazz.getEnumConstants())
                .filter((Dict<T> e) -> e.getText().equals(text))
                .map(Dict::getCode)
                .findAny().orElse(null);
    }

    /**
     * 通过code获取字典枚举实例
     *
     * @param clazz 枚举class
     * @param code  code
     * @param <T>   字典code类型
     * @param <R>   枚举类型
     * @return 字典枚举实例
     */
    @SuppressWarnings("unchecked")
    static <T extends Serializable, R extends Dict<T>> R getByCode(Class<? extends Dict<T>> clazz, T code) {
        return Stream.of(clazz.getEnumConstants())
                .filter((Dict<T> e) -> (e.getCode().equals(code)))
                .map(v -> (R) v)
                .findAny()
                .orElse(null);
    }

    /**
     * 获取给定的字典枚举项（常用下拉框数据请求）
     *
     * @param enums 可指定需要哪些项
     * @return List
     */
    @SafeVarargs
    static <T extends Serializable, E extends Dict<T>> List<Dict<T>> getItems(E... enums) {
        return Stream.of(enums)
                .map(DictPool::getDict)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有字典枚举项，除开指定的枚举
     *
     * @param exclude 指定排除的枚举
     * @return List
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    static <T extends Serializable, E extends Dict<T>> List<Dict<T>> getItemsExclude(E... exclude) {
        Class<Dict<T>> clazz = (Class<Dict<T>>) exclude.getClass().getComponentType();
        Dict<T>[] allEnum = clazz.getEnumConstants();
        List<Dict<T>> excludeList = Arrays.asList(exclude);
        return Stream.of(allEnum)
                .filter(e -> !excludeList.contains(e))
                .map(DictPool::getDict)
                .collect(Collectors.toList());
    }

    /**
     * 获取所有字典枚举项（常用下拉框数据请求）
     * 枚举值上标记@Deprecated的不会返回
     *
     * @param clazz 字典枚举类
     * @return List
     */
    static <T extends Serializable> List<Dict<T>> getAll(Class<? extends Dict<T>> clazz) {
        Map<String, Field> fieldCache = Arrays.stream(clazz.getDeclaredFields()).
                filter(Field::isEnumConstant).
                collect(Collectors.toMap(Field::getName, Function.identity()));
        Dict<T>[] allEnum = clazz.getEnumConstants();
        return Stream.of(allEnum)
                .filter(e -> !fieldCache.get(((Enum<?>) e).name()).isAnnotationPresent(Deprecated.class))
                .map(DictPool::getDict)
                .collect(Collectors.toList());
    }

    /**
     * 初始化
     *
     * @param code 字典编码
     * @param text 字典文本
     */
    default void init(T code, String text) {
        DictPool.putDict(this, code, text);
    }

    /**
     * 获取编码
     *
     * @return 编码
     */
    default T getCode() {
        return DictPool.getDict(this).getCode();
    }

    /**
     * 获取文本
     *
     * @return 文本
     */
    default String getText() {
        return DictPool.getDict(this).getText();
    }


    @UtilityClass
    class DictPool {
        private static final Map<Dict<?>, DictBean> DICT_MAP = new ConcurrentHashMap<>();


        static <T extends Serializable> void putDict(Dict<T> dict, T code, String text) {
            DICT_MAP.put(dict, new DictBean(code, text));
        }


        @SuppressWarnings("unchecked")
        static <K extends Dict<T>, T extends Serializable> Dict<T> getDict(K dict) {
            return (Dict<T>) DICT_MAP.get(dict);
        }


    }

}
