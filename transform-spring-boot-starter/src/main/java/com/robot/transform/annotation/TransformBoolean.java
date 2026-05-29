package com.robot.transform.annotation;

import com.robot.transform.transformer.BooleanTransformer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Boolean转换
 *
 * @author luozhan
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})

@Transform(transformer = BooleanTransformer.class)
public @interface TransformBoolean {
    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    @AliasFor(annotation = Transform.class)
    String from() default "";

    /**
     * true显示的文案
     */
    String trueLabel() default "是";

    /**
     * false显示的文案
     */
    String falseLabel() default "否";
}
