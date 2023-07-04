package com.robot.transform.util;

import com.robot.transform.annotation.Transform;
import com.robot.transform.component.TransformBean;
import com.robot.transform.transformer.Transformer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;



/**
 * 转换工具类
 *
 * @author R
 * @date 2022-9-27
 */
@Slf4j
@UtilityClass
public class TransformUtil {
    private static final Map<Field, TransformBean> TRANSFORM_CACHE = new ConcurrentHashMap<>(1000);

    /**
     * 转换对象，支持集合(Collection)或者单个bean
     * 并直接改变对象内部属性
     *
     * @param object 集合或单个bean
     */
    public static void transform(Object object) {
        if (object == null) {
            return;
        }
        log.debug("待转换对象类型{}", object.getClass().getSimpleName());
        // 不管是bean还是集合，先统一转成集合处理
        Collection<?> collection = (object instanceof Collection) ? (Collection<?>) object : Collections.singleton(object);
        collection.forEach(bean -> {
            Field[] declaredFields = bean.getClass().getDeclaredFields();
            // 循环处理需要转换的字段，字段上的注解链上需要有@Transform，且字段类型必须为String
            Arrays.stream(declaredFields)
                    // 只转换String类型的属性，其他类型的属性代表是嵌套情况需要过滤掉，后面处理
                    .filter(field -> field.getType() == String.class && AnnotatedElementUtils.isAnnotated(field, Transform.class))
                    .forEach(field -> transformField(bean, field));
            // 转换嵌套字段，字段上需要标注@Transform且字段类型不为String
            Arrays.stream(declaredFields)
                    .filter(field -> field.getType() != String.class && field.isAnnotationPresent(Transform.class))
                    // 递归转换，此时字段类型可能是Bean或者集合
                    .forEach(field -> transform(getFieldValue(bean, field.getName())));
        });
    }

    /**
     * 翻译属性
     *
     * @param bean  实例
     * @param field 需要被转换的属性（标注了@Transform的）
     */
    private void transformField(Object bean, Field field) {
        Transform transformAnnotation = getTransformAnnotation(field);
        // 拿原字段值，如果为null则跳过转换
        String originalFieldName = getOriginalFieldName(bean, field, transformAnnotation);
        Object originalFieldValue = getFieldValue(bean, originalFieldName);
        if (originalFieldValue == null) {
            return;
        }
        String result = transformField(field, originalFieldValue, transformAnnotation);
        // 转换结果写入当前字段
        setFieldValue(bean, field.getName(), result);
    }

    @SuppressWarnings("all")
    private String transformField(Field field, Object originalFieldValue, Transform transformAnnotation) {
        Class<? extends Transformer> transformerClass = transformAnnotation.transformer();
        // 从缓存中获取转换结果
        TransformBean transformBean = TRANSFORM_CACHE.computeIfAbsent(field, k -> new TransformBean(transformAnnotation));
        return transformBean.getTransformResultCache().computeIfAbsent(originalFieldValue, k -> {
            // 获取转换器及属性上的自定义注解
            Transformer<Object, Annotation> transformer = SpringContextUtil.getBean(transformerClass);
            Annotation annotation = getAnnotationOfField(transformer, field);
            // 执行转换逻辑
            return transformer.transform(originalFieldValue, annotation);
        });
    }

    /**
     * 获取转换器绑定的注解
     * 如果是SimpleTransformer的子类，则绑定的注解默认都是@Transform
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private Annotation getAnnotationOfField(Transformer<Object, Annotation> transformer, Field field) {
        // Transformer上有两个泛型，第一个是转换前的值类型，第二个是是自定义注解类型
        Class<?>[] genericTypes = GenericTypeResolver.resolveTypeArguments(transformer.getClass(), Transformer.class);
        Assert.isTrue(genericTypes != null, "转换错误！实现Transform接口必须指定泛型：" + transformer.getClass().getSimpleName());
        Class<?> transformerAnnotationType = genericTypes[1];
        // 如果转换器未绑定自定义注解，此处泛型就为默认的@Transform注解
        // 否则获取字段上标注的转换器绑定自定义注解
        return (transformerAnnotationType == Transform.class) ?  null : AnnotationUtils.getAnnotation(field, (Class<? extends Annotation>) transformerAnnotationType);
    }

    /**
     * 获取属性上的@Transform注解，如果有衍生注解则合并相同属性
     * ps.这里实际缓存的是注解的代理对象
     */
    private Transform getTransformAnnotation(Field field) {
        TransformBean transformBean = TRANSFORM_CACHE.computeIfAbsent(field, k -> new TransformBean(AnnotatedElementUtils.getMergedAnnotation(field, Transform.class)));
        return transformBean.getTransformAnnotation();
    }

    /**
     * 获取原字段名称
     * 如果没有显示指定将自动推断，并缓存
     */
    private String getOriginalFieldName(Object bean, Field field, Transform transform) {
        if (!transform.from().isEmpty()) {
            return transform.from();
        }
        // 没设置from值，智能推断关联的属性名，可能为xx、xxId、xxCode
        String transformFieldName = field.getName();
        String possibleNameA = StringUtils.replace(transformFieldName, "Name", "");
        String possibleNameB = StringUtils.replace(transformFieldName, "Name", "Id");
        String possibleNameC = StringUtils.replace(transformFieldName, "Name", "Code");
        List<String> possibleNameList = Arrays.asList(possibleNameA, possibleNameB, possibleNameC);
        // 匹配bean属性列表
        for (Field beanField : bean.getClass().getDeclaredFields()) {
            String fieldName = beanField.getName();
            if (possibleNameList.contains(fieldName)) {
                // 更新注解属性值，相当于做了个缓存
                updateAnnotationProxy(transform, "from", fieldName);
                return fieldName;
            }
        }

        throw new IllegalArgumentException("转换异常：无法自动推断" + transformFieldName + "的原始字段名，请使用注解@Transform的from属性指定被转换的字段");
    }

    /**
     * 动态更新注解值，用于缓存提升性能
     * 注意该方法仅针对spring代理的注解有效
     */
    @SuppressWarnings("all")
    private static void updateAnnotationProxy(Annotation annotation, String fieldName, Object value) {
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(annotation);
        // 代理类中有个valueCache属性，记录了原始注解的值，通过反射修改该值即可达到缓存效果
        Map<String, Object> memberValues = (Map<String, Object>) getFieldValue(invocationHandler, "valueCache");
        memberValues.put(fieldName, value);
    }

    /**
     * 读取属性值
     */
    private static Object getFieldValue(Object bean, String fieldName) {
        Field field = ReflectionUtils.findField(bean.getClass(), fieldName);
        Assert.notNull(field, "未找到" + bean.getClass().getSimpleName() + "中的" + fieldName + "属性");
        ReflectionUtils.makeAccessible(field);
        return ReflectionUtils.getField(field, bean);
    }

    /**
     * 给属性设值
     */
    private static <T> void setFieldValue(Object bean, String fieldName, T parameter) {
        Field field = ReflectionUtils.findField(bean.getClass(), fieldName);
        Assert.notNull(field, "未找到" + bean.getClass().getSimpleName() + "中的" + fieldName + "属性");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bean, parameter);
    }

}

