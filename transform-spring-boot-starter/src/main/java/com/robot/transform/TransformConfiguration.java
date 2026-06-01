package com.robot.transform;

import com.robot.transform.annotation.EnableTransform;
import com.robot.transform.strategy.TransformResponseBodyAdvice;
import com.robot.transform.util.SpringContextUtil;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

/**
 * GLOBAL模式配置：使用ResponseBodyAdvice实现
 */
@Configuration
@ConditionalOnWebApplication
@Import(SpringContextUtil.class)
public class TransformConfiguration implements ImportAware {

    /**
     * 从 @EnableTransform 注解上读取的 needAnnotation 配置
     */
    private boolean needAnnotation = false;

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        AnnotationAttributes attrs = AnnotationAttributes.fromMap(importMetadata.getAnnotationAttributes(EnableTransform.class.getName()));
        if (attrs != null) {
            this.needAnnotation = attrs.getBoolean("needAnnotation");
        }
    }

    @Bean
    public TransformResponseBodyAdvice transformResponseBodyAdvice() {
        return new TransformResponseBodyAdvice(needAnnotation);
    }


}