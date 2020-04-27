package com.robot.translator.core.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 用来获取spring实例
 *
 * @author luozhan
 * @create 2020-04
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
    private static ApplicationContext applicationContext;

    public static <T> T getBean(Class<T> clazz) {
        return applicationContext.getBean(clazz);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

}
