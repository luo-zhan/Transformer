package com.robot.transform.demo.controller.transform;

import com.robot.transform.annotation.Transform;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 年级转换（测试批量转换）
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})
@Transform(transformer = GradeTransformer.class)
public @interface TransformGrade {

    @AliasFor(annotation = Transform.class)
    String from() default "";
}
