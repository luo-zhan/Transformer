package com.robot.transform.util;

import com.robot.transform.component.TransformClass;
import com.robot.transform.component.TransformSession;
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
     * <p>
     * 每次转换结束后清空转换结果缓存；如需多次转换间共享缓存（如分页导出场景），
     * 请使用 {@link #openSession()} 开启会话
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
        // 清空缓存；会话期间延迟到会话关闭时统一清空，多次转换间共享缓存
        if (!TransformSession.recordIfActive(transformClassRecorder)) {
            transformClassRecorder.forEach(TransformClass::clearCache);
        }
    }

    /**
     * 开启转换会话（线程级别）
     * <p>
     * 适用于大数据量分页导出等需要多次调用 {@link #transform(Object)} 的场景：
     * 会话期间转换结果缓存跨多次转换共享，相同的原始值（如字典code）只触发一次实际转换，
     * 会话关闭时统一清空缓存，同一导出文件内字典口径保持一致
     * <p>
     * 推荐配合try-with-resources使用，避免忘记关闭导致缓存泄漏到线程池中的后续任务：
     * <pre>{@code
     * try (TransformSession session = TransformUtil.openSession()) {
     *     while (!(page = studentService.page(pageNum++, 1000)).isEmpty()) {
     *         TransformUtil.transform(page);
     *         excelWriter.write(page);
     *     }
     * }
     * }</pre>
     *
     * @return 会话，close时清空会话期间累计的转换结果缓存
     */
    public static TransformSession openSession() {
        return TransformSession.open();
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

