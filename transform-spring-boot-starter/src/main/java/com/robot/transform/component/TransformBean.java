package com.robot.transform.component;

import com.robot.transform.annotation.Transform;
import com.robot.transform.transformer.Transformer;
import lombok.Data;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 转换bean，封装一些必要字段
 *
 * @author R
 */
@Data
public class TransformBean {
    private String from;
    private Transformer<Object, Annotation> transformer;
    private Transform transformAnnotation;
    private Map<Object, String> transformResultCache = new ConcurrentHashMap<>();

   public TransformBean(Transform transformAnnotation) {
        this.transformAnnotation = transformAnnotation;
    }
}
