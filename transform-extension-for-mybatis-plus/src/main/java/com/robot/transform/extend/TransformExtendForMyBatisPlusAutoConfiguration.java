package com.robot.transform.extend;

import com.robot.transform.extend.converter.PageToListConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换器配置类
 *
 * @author R
 */
@Configuration
public class TransformExtendForMyBatisPlusAutoConfiguration {

    @Bean
    public PageToListConverter<Object> pageConvert() {
        return new PageToListConverter<>();
    }


}
