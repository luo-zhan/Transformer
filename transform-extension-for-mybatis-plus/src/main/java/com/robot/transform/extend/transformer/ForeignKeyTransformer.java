package com.robot.transform.extend.transformer;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.robot.transform.extend.annotation.TransformFK;
import com.robot.transform.transformer.BatchTransformer;
import com.robot.transform.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * 外键转换器
 * 可在本地数据将id转换成另一个指定属性
 *
 * @author R
 * @see TransformFK （Mapper转换注解）
 */
@Slf4j
public class ForeignKeyTransformer implements BatchTransformer<Serializable, TransformFK> {

    @Override
    @NonNull
    public Map<Serializable, String> batchTransform(@NonNull Set<Serializable> ids, @NonNull TransformFK annotation) {
        if (ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Class<? extends BaseMapper<?>> clazz = annotation.mapper();
        BaseMapper<?> mapper = SpringContextUtil.getBean(clazz);
        List<?> list = mapper.selectBatchIds(ids);
        if (list == null || list.isEmpty()) {
            log.warn("转换警告：{}类根据ids={}，找不到任何数据！", clazz.getSimpleName(), ids);
            return Collections.emptyMap();
        }

        Field idField = findIdField(list.get(0).getClass());
        Assert.notNull(idField, "找不到id字段，请检查实体类是否包含id字段或@TableId注解：" + list.get(0).getClass());
        ReflectionUtils.makeAccessible(idField);

        Field targetField = ReflectionUtils.findField(list.get(0).getClass(), annotation.to());
        Assert.notNull(targetField, "找不到该属性，请检查注解@TransformFK的to字段配置：" + annotation);
        ReflectionUtils.makeAccessible(targetField);

        Map<Serializable, String> result = new HashMap<>(list.size());
        for (Object entity : list) {
            Serializable entityId = (Serializable) ReflectionUtils.getField(idField, entity);
            Object value = ReflectionUtils.getField(targetField, entity);
            result.put(entityId, value == null ? null : value.toString());
        }
        return result;
    }

    private Field findIdField(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(TableId.class)) {
                return field;
            }
        }
        return ReflectionUtils.findField(clazz, "id");
    }
}
