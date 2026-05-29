package com.robot.transform.annotation;

import com.robot.transform.TransformConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 开启字段转换功能
 *
 * @author R
 * @since 2026/5/29
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(TransformConfiguration.class)
public @interface EnableTransform {

    /**
     * 是否需要@Transform注解开启转换
     */
    boolean needAnnotation() default false;
}
