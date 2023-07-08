package com.robot.transform.component;

import com.robot.transform.annotation.Transform;
import com.robot.transform.transformer.Transformer;
import com.robot.transform.util.SpringContextUtil;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.Assert;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 标记为@Transform的属性的封装
 * 缓存相关信息，提高性能
 *
 * @author R
 */
@Data
public class TransformField<T> {
    private Field field;
    private Field originField;
    private Transformer<T, Annotation> transformer;
    private Annotation transformAnnotation;
    /**
     * 转换结果缓存，线程级别
     */
    private ThreadLocal<Map<T, String>> transformResultCache = ThreadLocal.withInitial(ConcurrentHashMap::new);

    @SuppressWarnings("unchecked")
    public TransformField(Field field) {
        this.field = field;
        // 搜索属性上所有注解继承关系，获取最终合并注解属性后的@Transform注解实例（主要是拿到from和transformer两个属性）
        Transform mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, Transform.class);
        Assert.notNull(mergedAnnotation, "字段" + field.getName() + "上必须标注@Transform注解或其衍生注解");
        String originFieldName = mergedAnnotation.from().isEmpty() ? analyzeOriginFieldName(field) : mergedAnnotation.from();
        this.originField = FieldUtils.getField(field.getDeclaringClass(), originFieldName, true);
        Class<? extends Transformer<T, Annotation>> transformerClass = (Class<? extends Transformer<T, Annotation>>) mergedAnnotation.transformer();
        // 通过spring容器拿转换器实例
        this.transformer = SpringContextUtil.getBean(transformerClass);
        // 获取自定义注解类型（Transformer上有两个泛型，第一个是转换前的值类型，第二个是是自定义注解类型）
        ResolvableType resolvableType = ResolvableType.forClass(Transformer.class, transformerClass);
        Class<? extends Annotation> customTransformAnnotationType = (Class<? extends Annotation>) resolvableType.getGeneric(1).resolve();
        Assert.notNull(customTransformAnnotationType, "实现Transform接口时必须指定泛型：" + transformer.getClass().getSimpleName());
        this.transformAnnotation = field.getAnnotation(customTransformAnnotationType);
    }

    /**
     * 转换结果，并缓存
     *
     * @param bean 对象
     */
    @SuppressWarnings("unchecked")
    public void transform(Object bean) throws IllegalAccessException {
        T originalValue = (T) FieldUtils.readField(originField, bean, true);
        if (originalValue == null) {
            return;
        }
        String transformResult = transformResultCache.get().computeIfAbsent(originalValue, k -> transformer.transform(originalValue, transformAnnotation));
        FieldUtils.writeField(field, bean, transformResult, true);
    }

    /**
     * 清除缓存
     */
    public void clearCache() {
        transformResultCache.remove();
    }

    /**
     * 根据当前字段，智能分析原字段名称
     */
    private String analyzeOriginFieldName(Field field) {
        Class<?> beanClass = field.getDeclaringClass();
        String fieldName = field.getName();
        // 没设置from值，智能推断关联的属性名，可能为xx、xxId、xxCode
        String possibleNameA = StringUtils.replace(fieldName, "Name", "");
        String possibleNameB = StringUtils.replace(fieldName, "Name", "Id");
        String possibleNameC = StringUtils.replace(fieldName, "Name", "Code");
        List<String> possibleNameList = Arrays.asList(possibleNameA, possibleNameB, possibleNameC);
        // 匹配bean属性列表
        for (Field beanField : FieldUtils.getAllFields(beanClass)) {
            if (possibleNameList.contains(beanField.getName())) {
                return beanField.getName();
            }
        }
        throw new IllegalArgumentException("转换异常：无法自动推断" + fieldName + "的原始字段名，请使用注解@Transform的from属性指定被转换的字段");
    }
}
