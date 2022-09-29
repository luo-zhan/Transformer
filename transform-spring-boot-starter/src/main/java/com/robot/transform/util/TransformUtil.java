package com.robot.transform.util;


import com.robot.transform.annotation.Transform;
import com.robot.transform.transformer.Transformer;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.robot.transform.util.LambdaUtil.sure;


/**
 * 转换工具类
 *
 * @author R
 * @date 2022-9-27
 */

/**
 * 转换工具类
 *
 * @author R
 * @date 2022-9-27
 */
@Slf4j
@UtilityClass
public class TransformUtil {
    private static final Map<Field, Transform> ANNOTATION_CACHE = new ConcurrentHashMap<>(1000);

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
                    .forEach(field -> {
                        // 递归转换
                        Object propertyValue = sure(() -> readMethodInvoke(bean, field.getName()));
                        transform(propertyValue);
                    });
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
        Object originalFieldValue = sure(() -> readMethodInvoke(bean, originalFieldName));
        if (originalFieldValue == null) {
            return;
        }
        @SuppressWarnings("all")
        Class<? extends Transformer> transformerClass = transformAnnotation.transformer();
        Assert.isTrue(transformerClass != null, "注解配置有误，" + transformAnnotation.getClass().getSimpleName() + "的transformer未配置具体实现类");
        @SuppressWarnings("unchecked")
        // 获取转换器及转换器绑定的注解
        Transformer<Object, Annotation> transformer = SpringContextUtil.getBean(transformerClass);
        Annotation annotation = getAnnotationOfTransformer(transformer, field, transformAnnotation);
        // 执行转换逻辑
        String result = transformer.transform(originalFieldValue, annotation);
        // 转换结果写入当前字段
        sure(() -> writeMethodInvoke(bean, field.getName(), String.class, result));
    }

    /**
     * 获取转换器绑定的注解
     * 如果是SimpleTransformer的子类，则绑定的注解默认都是@Transform
     */
    @Nullable
    private Annotation getAnnotationOfTransformer(Transformer<Object, Annotation> transformer, Field field, Transform transformAnnotation) {
        // Transformer上有两个泛型，第一个是转换前的值类型，第二个是是自定义注解类型
        Class<?>[] genericTypes = GenericTypeResolver.resolveTypeArguments(transformer.getClass(), Transformer.class);
        Assert.isTrue(genericTypes != null, "转换错误！实现Transform接口必须指定泛型：" + transformer.getClass().getSimpleName());
        Class<?> transformerAnnotationType = genericTypes[1];
        @SuppressWarnings("unchecked")
        // 如果转换器未绑定自定义注解，此处泛型就为默认的@Transform注解
        // 否则获取字段上标注的转换器绑定自定义注解
        Annotation annotation = (transformerAnnotationType == Transform.class) ?
                transformAnnotation : AnnotationUtils.getAnnotation(field, (Class<? extends Annotation>) transformerAnnotationType);
        checkAnnotationOfTransformer(annotation, field, transformerAnnotationType);
        return annotation;
    }

    /**
     * 检查自定义注解
     */
    private void checkAnnotationOfTransformer(Annotation annotation, Field field, Class<?> transformerAnnotationType) {
        if (annotation == null) {
            String beanName = field.getDeclaringClass().getSimpleName();
            String fieldName = field.getName();
            String annotationName = transformerAnnotationType.getSimpleName();
            if (transformerAnnotationType.getDeclaredMethods().length > 1) {
                // 如果自定义注解的方法超过一个，说明该注解有自定义功能，此时只用@Translate是错误的，应直接报错提示开发者改正
                throw new IllegalArgumentException(beanName + "属性" + fieldName + "上的注解配置错误，缺少注解@" + annotationName);
            } else {
                // 如果自定义注解的方法只有一个（就是from），说明该自定义注解只是为了简化原注解，无新增自定义功能
                // 此时仍可以使用@Translate注解，从而在转换器升级绑定了自定义注解时还能保持向下兼容
                log.warn(MessageFormat.format("{0}类的属性{1}上的「@Transform」注解配置方式已过时，建议替换为新注解「@{2}」", beanName, fieldName, annotationName));
            }
        }
    }

    /**
     * 获取属性上的@Transform注解，如果有衍生注解则合并相同属性
     * ps.这里实际缓存的是注解的代理对象
     */
    private Transform getTransformAnnotation(Field field) {
        return ANNOTATION_CACHE.computeIfAbsent(field, k -> AnnotatedElementUtils.getMergedAnnotation(field, Transform.class));
    }

    /**
     * 获取原字段名称
     * 如果没有显示指定将自动推断
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
        List<String> nameList = Arrays.asList(possibleNameA, possibleNameB, possibleNameC);
        // 匹配bean属性列表
        for (Field beanField : bean.getClass().getDeclaredFields()) {
            String fieldName = beanField.getName();
            if (nameList.contains(fieldName)) {
                // 更新注解属性值，相当于做了个缓存
                updateAnnotationProxy(transform, "from", fieldName);
                return fieldName;
            }
        }

        throw new IllegalArgumentException("转换异常：无法自动推断" + transformFieldName + "的原始字段名，请使用注解@Transform的from属性指定被转换的字段");
    }

    /**
     * 动态更新注解值，用于缓存提升性能
     * 注意该方法仅针对spring对注解的代理有效
     */
    @SuppressWarnings("all")
    private static void updateAnnotationProxy(Annotation annotation, String fieldName, Object value) {
        InvocationHandler h = Proxy.getInvocationHandler(annotation);
        // 代理类中有个valueCache属性，记录了原始注解的值
        Field field = sure(() -> h.getClass().getDeclaredField("valueCache"));
        field.setAccessible(true);
        Map<String, Object> memberValues = (Map<String, Object>) sure(() -> field.get(h));
        memberValues.put(fieldName, value);
    }


    /**
     * 读取属性值
     */
    public static Object readMethodInvoke(Object bean, String fieldName) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = bean.getClass().getDeclaredMethod("get" + capitalize(fieldName));
        return method.invoke(bean);
    }

    /**
     * 给属性设值
     */
    public static <T> void writeMethodInvoke(Object bean, String fieldName, Class<T> parameterType, T parameter) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = bean.getClass().getDeclaredMethod("set" + capitalize(fieldName), parameterType);
        method.invoke(bean, parameter);
    }

    /**
     * 首字母大写
     */
    private static String capitalize(String name) {
        if (name == null || name.length() == 0) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}

