package com.robot.translator.core;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

/**
 * 字典翻译
 * 在方法上定义@Tanslator注解，对方法返回值进行翻译
 *
 * @author luozhan
 * @create 2020-04
 * @see Translator
 */
@Aspect
@Component
public class TranslateAspect {
    @Pointcut("@annotation(com.robot.translator.core.annotation.Translator)")
    public void pointCut() {
    }

    @AfterReturning(pointcut = "pointCut()", returning = "object")
    public void doAfter(JoinPoint joinPoint, Object object) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        com.robot.translator.core.annotation.Translator config = signature.getMethod().getAnnotation(com.robot.translator.core.annotation.Translator.class);
        Translator.parse(object, config.value());
    }
}
