package com.robot.transform.extend.annotation;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.transform.annotation.Transform;
import com.robot.transform.extend.transformer.ForeignKeyTransformer;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 外键转换
 * 使用指定Mapper将外键id转换成外键表的另一个指定字段
 *
 * @author R
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.FIELD})

@Transform(transformer = ForeignKeyTransformer.class)
public @interface TransformFK {
    /**
     * 来源字段
     * <p>
     * 默认自动推断（推断规则：如注解标注的字段是userName，自动推断结果为“user”，“userId”或“userCode”）
     */
    @AliasFor(annotation = Transform.class)
    String from() default "";

    /**
     * 外键表的 mapper-class，必须实现自BaseMapper接口
     */
    Class<? extends BaseMapper<?>> mapper();

    /**
     * 指定外键表bean的字段（想转换为哪个字段）
     */
    String to();
}
