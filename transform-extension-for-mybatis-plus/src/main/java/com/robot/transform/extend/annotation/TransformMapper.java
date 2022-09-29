package com.robot.transform.extend.annotation;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.transform.annotation.Transform;
import com.robot.transform.extend.transformer.MapperTransformer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * Mapper转换，使用指定Mapper将id转换成另一个指定字段
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})

@Transform(transformer = MapperTransformer.class)
public @interface TransformMapper {
    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    @AliasFor(annotation = Transform.class)
    String from() default "";

    /**
     * mapper class，必须实现自BaseMapper接口
     */
    Class<? extends BaseMapper<?>> value();

    /**
     * bean的目标字段
     */
    String targetField();
}
