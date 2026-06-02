package com.robot.transform.transformer;


import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;

/**
 * 转换器接口
 * 支持自定义注解
 *
 * @author R
 * @since 2022-9-27
 */
public interface Transformer<T, A extends Annotation> {


    /**
     * 转换
     *
     * @param originalValue 转换之前的原始值
     * @param annotation     注解
     * @return 转换后的值
     */
    default String transform(@NonNull T originalValue, A annotation) {
        return transform(originalValue);
    }

    /**
     * 转换
     *
     * @param originalValue 原始值
     * @return 转换后的值
     */
    default String transform(@NonNull T originalValue) {
        throw new UnsupportedOperationException(
                "Transformer实现类必须至少实现一个transform方法！\n" +
                        "• 如果需要使用注解参数，请实现：transform(T, A)\n" +
                        "• 如果不需要注解参数，请实现：transform(T)"
        );
    }


}
