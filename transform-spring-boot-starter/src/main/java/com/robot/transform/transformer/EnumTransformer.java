package com.robot.transform.transformer;

import com.robot.transform.annotation.TransformEnum;
import com.robot.transform.component.Dict;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;


/**
 * 枚举转换器
 *
 * @author R
 */
@Component
public class EnumTransformer<T> implements Transformer<T, TransformEnum> {

    @Override
    @SuppressWarnings("unchecked")
    public String transform(@Nonnull T enumCode, @Nonnull TransformEnum annotation) {
        return Dict.getTextByCode((Class<? extends Dict<T>>) annotation.value(), enumCode);
    }


}
