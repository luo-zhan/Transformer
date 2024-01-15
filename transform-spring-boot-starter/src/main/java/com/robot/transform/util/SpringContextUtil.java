package com.robot.transform.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

/**
 * Spring上下文工具
 *
 * @author R
 * @since 2022年3月18日
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {

    private static ApplicationContext context;


    /**
     * 获取示例，如果找不到会报错
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    private static void setContext(ApplicationContext context) {
        SpringContextUtil.context = context;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        setContext(applicationContext);
    }

}
