package com.robot.transform.util;

import com.robot.transform.component.TransformClass;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


/**
 * 转换工具类
 *
 * @author R
 * @since 2022-9-27
 */
@Slf4j
@UtilityClass
public class TransformUtil {

    /**
     * 转换类的缓存，提高性能
     */
    private static final Map<Class<?>, TransformClass> TRANSFORM_CLASS_CACHE = new ConcurrentHashMap<>(512);

    /**
     * 转换对象，支持集合(Collection)或者单个bean
     * 并直接改变对象内部属性
     *
     * @param object 集合或单个bean
     */
    public static void transform(Object object) throws IllegalAccessException {
        TransformClass transformClass = getTransformClassOfObject(object);
        if (transformClass == null) {
            // 获取不到转换类，表示object为null或者空集合
            return;
        }
        Set<TransformClass> transformClassRecorder = new HashSet<>();
        // 字段转换，并记录转换类
        transform(object, transformClass, transformClassRecorder);
        // 清空缓存
        transformClassRecorder.forEach(TransformClass::clearCache);
    }

    /**
     * 对象属性转换，其中嵌套属性使用递归方式转换
     *
     * @param obj                    需要转换的对象，集合或者单个bean
     * @param transformClass         转换类
     * @param transformClassRecorder 记录转换类，为了最后清除缓存
     */
    private void transform(Object obj, TransformClass transformClass, Set<TransformClass> transformClassRecorder) throws IllegalAccessException {
        boolean isNullOrEmptyCollection = obj == null || obj instanceof Collection && ((Collection<?>) obj).isEmpty();
        if (isNullOrEmptyCollection) {
            return;
        }
        // 转换
        transformClass.transform(obj);
        transformClassRecorder.add(transformClass);
        // 处理嵌套转换，拿到当前类的所有嵌套转换属性和对应的转换类
        Map<Field, Class<?>> nestTransformFields = transformClass.getNestTransformFields();
        Collection<?> collection = (obj instanceof Collection) ? (Collection<?>) obj : Collections.singletonList(obj);
        for (Map.Entry<Field, Class<?>> entry : nestTransformFields.entrySet()) {
            Field field = entry.getKey();
            TransformClass transformClassOfField = getTransformClassFromClass(entry.getValue());
            for (Object bean : collection) {
                // 递归
                Object fieldValue = FieldUtils.readField(field, bean, true);
                transform(fieldValue, transformClassOfField, transformClassRecorder);
            }

        }

    }

    /**
     * 根据class获取TransformClass
     *
     * @param obj obj 对象或者集合，集合取第一个元素的class
     * @return TransformClass 转换类，如果是空集合或者空对象，返回null
     */
    private TransformClass getTransformClassOfObject(Object obj) {
        if (obj == null) {
            return null;
        }
        if ((obj instanceof Collection)) {
            // 集合，取第一个元素的class，为空返回null
            Iterator<?> iterator = ((Collection<?>) obj).iterator();
            if (iterator.hasNext()) {
                Class<?> clazz = iterator.next().getClass();
                return getTransformClassFromClass(clazz);
            } else {
                return null;
            }
        }
        return getTransformClassFromClass(obj.getClass());

    }

    public static TransformClass getTransformClassFromClass(Class<?> clazz) {
        return TRANSFORM_CLASS_CACHE.computeIfAbsent(clazz, TransformClass::new);
    }
}

