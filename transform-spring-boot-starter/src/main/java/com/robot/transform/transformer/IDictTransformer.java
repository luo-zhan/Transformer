package com.robot.transform.transformer;

import com.robot.transform.annotation.TransformDict;
import org.springframework.lang.NonNull;


/**
 * 数据字典转换器接口
 * 注意需要自行实现接口后（名字必须是DictTransformer）并注入spring容器才能正常使用
 *
 * @author R
 */
public interface IDictTransformer<T> extends Transformer<T, TransformDict> {
    /**
     * 转换
     *
     * @param originalValue 转换之前的原始值
     * @param transformDict 注解
     * @return 转换后的值
     */
    @Override
    default String transform(@NonNull T originalValue, TransformDict transformDict) {
        return transform(originalValue, transformDict.group());
    }

    /**
     * 转换
     *
     * @param originalValue 原始值
     * @param group         组名
     * @return 转换后的值
     */
    String transform(@NonNull T originalValue, String group);


}
