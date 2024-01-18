package com.robot.transform.extend;

import com.robot.transform.extend.unwrapper.PageUnWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换器配置类
 *
 * @author R
 */
@Configuration
public class TransformExtendForMyBatisFlexAutoConfiguration {

    /**
     * 注册IPage解包器
     */
    @Bean
    public PageUnWrapper<Object> iPageUnWrapper() {
        return new PageUnWrapper<>();
    }


}
