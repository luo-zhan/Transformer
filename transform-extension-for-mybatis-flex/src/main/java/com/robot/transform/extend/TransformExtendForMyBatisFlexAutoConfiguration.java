package com.robot.transform.extend;

import com.robot.transform.extend.transformer.ForeignKeyTransformer;
import com.robot.transform.extend.unwrapper.PageUnWrapper;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

/**
 * 转换器配置类
 *
 * @author R
 */
@AutoConfiguration
public class TransformExtendForMyBatisFlexAutoConfiguration {

    /**
     * 注册IPage解包器
     */
    @Bean
    public PageUnWrapper<Object> iPageUnWrapper() {
        return new PageUnWrapper<>();
    }

    /**
     * 注册外键转换器
     */
    @Bean
    public ForeignKeyTransformer foreignKeyTransformer() {
        return new ForeignKeyTransformer();
    }

}
