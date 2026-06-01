package com.robot.transform.strategy;

import com.robot.transform.annotation.Transform;
import com.robot.transform.util.TransformUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * 全局响应体转换
 *
 * @author R
 * @since 2026/5/29
 */
@Slf4j
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TransformResponseBodyAdvice implements ResponseBodyAdvice<Object> {

    @Resource
    private ConversionService mvcConversionService;

    /**
     * 是否需要 @Transform 注解才进行转换，由 @EnableTransform 注解控制
     */
    private final boolean needAnnotation;

    public TransformResponseBodyAdvice(boolean needAnnotation) {
        this.needAnnotation = needAnnotation;
    }

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Method method = returnType.getMethod();
        if (method == null) {
            return false;
        }
        // 需要注解时，仅标有 @Transform 的方法进行转换
        if (needAnnotation) {
            return AnnotationUtils.findAnnotation(method, Transform.class) != null;
        }
        // 不需要注解时，默认全部进行转换
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body == null || returnType.getMethod() == null) {
            return null;
        }

        try {
            // 使用容器中的转换器进行返回值解包
            Object result = mvcConversionService.convert(body, Object.class);
            TransformUtil.transform(result);
            return result;
        } catch (Exception e) {
            // 异常不影响接口正常返回，仅记录错误日志
            log.error("出参转换失败:类型={}, 错误={}", returnType.getMethod().getGenericReturnType(), e.getMessage(), e);
            // 转换失败时返回原始对象，保证接口可用性
            return body;
        }
    }
}
