package com.robot.transform.transformer;

import org.springframework.lang.NonNull;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * 批量转换器接口
 * 继承 Transformer，支持一次性批量转换多个原始值，减少重复查询
 *
 * @author R
 * @since 2026-6-1
 */
public interface BatchTransformer<T, A extends Annotation> extends Transformer<T, A> {

    /**
     * 批量转换（含注解参数）
     *
     * @param originalValues 转换之前的原始值集合
     * @param annotation     自定义注解
     * @return 原始值到翻译后值的映射
     */
    @NonNull
    default Map<T, String> batchTransform(@NonNull Set<T> originalValues, A annotation) {
        return batchTransform(originalValues);
    }

    /**
     * 批量转换（不含注解参数）
     *
     * @param originalValues 转换之前的原始值集合
     * @return 原始值到翻译后值的映射
     */
    @NonNull
    default Map<T, String> batchTransform(@NonNull Set<T> originalValues) {
        throw new UnsupportedOperationException(
                "BatchTransformer实现类必须至少实现一个batchTransform方法！\n" +
                        "• 如果需要使用注解参数，请实现：batchTransform(Set<T>, A)\n" +
                        "• 如果不需要注解参数，请实现：batchTransform(Set<T>)"
        );
    }

    /**
     * 基于批量转换方法实现的单值转换方法，如果有性能更好的直接转换方法可以重写该方法
     *
     * @param originalValue 转换之前的原始值
     * @param annotation    注解
     * @return 转换之后的值
     */

    @Override
    default String transform(@NonNull T originalValue, A annotation) {
        Map<T, String> map = batchTransform(Collections.singleton(originalValue), annotation);
        if (!map.isEmpty()) {
            return map.get(originalValue);
        }
        return null;
    }
}
