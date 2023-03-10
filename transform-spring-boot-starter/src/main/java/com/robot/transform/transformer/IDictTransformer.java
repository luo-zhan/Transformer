package com.robot.transform.transformer;

import com.robot.transform.annotation.TransformDict;

import javax.annotation.Nonnull;

/**
 * 数据字典转换器接口
 *
 * @author R
 */
public interface IDictTransformer<T> extends Transformer<T, TransformDict,String> {
    /**
     * 转换
     *
     * @param originalValue 转换之前的原始值
     * @param transformDict 注解
     * @return 转换后的值
     */
    @Override
    default String transform(@Nonnull T originalValue, TransformDict transformDict) {
        return transform(originalValue, transformDict.group());
    }

    /**
     * 转换
     *
     * @param originalValue 原始值
     * @param group         组名
     * @return 转换后的值
     */
    String transform(@Nonnull T originalValue, String group);


}
