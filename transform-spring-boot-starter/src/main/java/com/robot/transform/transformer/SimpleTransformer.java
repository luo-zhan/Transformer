package com.robot.transform.transformer;

import com.robot.transform.annotation.Transform;
import org.springframework.lang.NonNull;


/**
 * 简单转换器接口
 * 自定义转换场景中如果自定义注解里不需要额外属性（除开from属性），直接实现该接口即可
 *
 * @author R
 */
public interface SimpleTransformer<T> extends Transformer<T, Transform> {
    /**
     * 转换
     *
     * @param originalValue 转换之前的原始值
     * @param transform     注解
     * @return 转换后的值
     */
    @Override
    default String transform(@NonNull T originalValue, @NonNull Transform transform) {
        return transform(originalValue);
    }

    /**
     * 转换
     *
     * @param originalValue 原始值
     * @return 转换后的值
     */
    String transform(@NonNull T originalValue);


}
