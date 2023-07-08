package com.robot.framework.demo.controller.transform;

import com.robot.transform.annotation.Transform;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 班级转换（自定义转换注解）
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})


@Transform(transformer = ClassTransformer.class)
public @interface TransformClass {

    @AliasFor(annotation = Transform.class)
    String from() default "";


}
