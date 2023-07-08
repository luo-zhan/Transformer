package com.robot.transform.transformer;

import com.robot.transform.annotation.TransformEnum;
import com.robot.transform.component.Dict;
import org.springframework.lang.NonNull;

import java.io.Serializable;


/**
 * 枚举转换器
 *
 * @author R
 */
public class EnumTransformer<T extends Serializable> implements Transformer<T, TransformEnum> {

    @Override
    @SuppressWarnings("unchecked")
    public String transform(@NonNull T enumCode, TransformEnum annotation) {
        return Dict.getTextByCode((Class<? extends Dict<T>>) annotation.value(), enumCode);
    }

}
