package com.robot.transform.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 数据字典转换
 * 注意：该注解的转换器需要由业务来定义，如果有数据字典转换的需求，先定义一个数据字典的转换器
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})

@Transform
public @interface TransformDict {

    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    @AliasFor(annotation = Transform.class)
    String from() default "";

    /**
     * 组名
     * 数据字典中有许多不同的组，如”sex“，”orderType“等等
     */
    String group();

}
