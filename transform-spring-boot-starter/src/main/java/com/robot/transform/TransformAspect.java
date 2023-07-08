package com.robot.transform;

import com.robot.transform.annotation.Transform;
import com.robot.transform.util.TransformUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.convert.support.GenericConversionService;

import javax.annotation.Resource;

/**
 * 转换器AOP
 *
 * @author R
 * @date 2022-9-27
 */
@Aspect
@Slf4j
public class TransformAspect {

    @Resource
    private GenericConversionService genericConversionService;

    @AfterReturning(pointcut = "@annotation(transformAnnotation)", returning = "returnValue")
    public void doAfter(Object returnValue, Transform transformAnnotation) throws IllegalAccessException {
        long l = System.currentTimeMillis();
        // 获取容器中的转换器进行返回值解包，注意此处返回结果可能是Bean也可能是集合
        Object result = genericConversionService.convert(returnValue, Object.class);
        TransformUtil.transform(result);
        long time = System.currentTimeMillis() - l;
        log.debug("转换耗时：{}ms", time);
    }
}
