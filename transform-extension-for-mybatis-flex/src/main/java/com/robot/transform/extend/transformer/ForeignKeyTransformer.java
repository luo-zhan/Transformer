package com.robot.transform.extend.transformer;


import com.mybatisflex.core.BaseMapper;
import com.robot.transform.extend.annotation.TransformFK;
import com.robot.transform.transformer.Transformer;
import com.robot.transform.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * 外键转换器
 * 可在本地数据将id转换成另一个指定属性
 *
 * @author R
 * @see TransformFK （Mapper转换注解）
 */
@Slf4j
public class ForeignKeyTransformer implements Transformer<Serializable, TransformFK> {

    @Override
    public String transform(@NonNull Serializable id, @NonNull TransformFK annotation) {
        Class<? extends BaseMapper<?>> clazz = annotation.mapper();
        BaseMapper<?> mapper = SpringContextUtil.getBean(clazz);
        Object entity = mapper.selectOneById(id);
        if (entity == null) {
            log.warn("转换警告：{}类根据id={}，找不到任何数据！", clazz.getSimpleName(), id);
            return null;
        }
        Field field = ReflectionUtils.findField(entity.getClass(), annotation.to());
        Assert.notNull(field, "找不到该属性，请检查注解@TransformFK的field字段配置：" + annotation);
        ReflectionUtils.makeAccessible(field);
        return (String) ReflectionUtils.getField(field, entity);
    }
}