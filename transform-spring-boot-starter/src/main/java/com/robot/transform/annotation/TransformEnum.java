package com.robot.transform.annotation;


import com.robot.dict.Dict;
import com.robot.transform.transformer.EnumTransformer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 枚举转换
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})

@Transform(transformer = EnumTransformer.class)
public @interface TransformEnum {
    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    @AliasFor(annotation = Transform.class)
    String from() default "";

    /**
     * 枚举class，必须实现了Dict接口
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Dict> value();

}
