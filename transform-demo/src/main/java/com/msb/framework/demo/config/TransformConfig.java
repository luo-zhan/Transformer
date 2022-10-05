package com.msb.framework.demo.config;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.msb.framework.demo.bean.ResultWrapper;
import com.robot.transform.component.UnWrapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 转换插件配置
 *
 * @author R
 */
@Configuration
public class TransformConfig {

    @Bean
    public ResultUnWrapper resultUnWrapper() {
        return new ResultUnWrapper();
    }

    /**
     * 注册ResultWrapper的解包器
     */
    public static class ResultUnWrapper implements UnWrapper<ResultWrapper<?>> {
        @Override
        public Object unWrap(ResultWrapper<?> source) {
            Object data = source.getData();
            if (data instanceof IPage) {
                return ((IPage<?>) data).getRecords();
            }
            // List或bean直接返回
            return data;
        }
    }

}
