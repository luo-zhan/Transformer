package com.robot.translator.core.annotation;

import java.lang.annotation.*;

/**
 * 【字典翻译注解】指定在方法上，对方法返回值进行翻译
 *
 * @author luozhan
 * @date 2020-04
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Translator {

    /**
     * 指定翻译配置类，可传多个，一般为实体class
     */
    Class<?>[] value() default void.class;

}
