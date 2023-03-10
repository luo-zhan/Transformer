package com.robot.transform.annotation;

import com.robot.transform.transformer.Transformer;

import java.lang.annotation.*;

/**
 * 转换注解
 * 最基本的注解，可以被其他自定义注解衍生
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.TYPE})
public @interface Transform {

    /**
     * 指定转换器
     */
    Class<? extends Transformer> transformer() default Transformer.class;

    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    String from() default "";

    boolean async() default false;

    int cacheTime() default -1;

    Class returnType() default String.class;
}
