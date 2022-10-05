package com.robot.transform.extend;

import com.robot.transform.extend.unwrapper.IPageUnWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换器配置类
 *
 * @author R
 */
@Configuration
public class TransformExtendForMyBatisPlusAutoConfiguration {

    /**
     * 注册IPage解包器
     */
    @Bean
    public IPageUnWrapper<Object> iPageUnWrapper() {
        return new IPageUnWrapper<>();
    }


}
