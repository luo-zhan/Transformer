package com.robot.transform.transformer;

import com.robot.transform.annotation.TransformBoolean;
import org.springframework.lang.NonNull;


/**
 * Boolean转换器
 *
 * @author luozhan
 * @since 2026/5/6
 */
public class BooleanTransformer implements Transformer<Boolean, TransformBoolean> {

    @Override
    public String transform(@NonNull Boolean code, TransformBoolean annotation) {
        return code ? annotation.trueLabel() : annotation.falseLabel();

    }

}
