package com.msb.framework.demo.controller.transform;

import com.msb.framework.demo.service.ClassService;
import com.robot.transform.transformer.SimpleTransformer;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.util.Optional;

/**
 * 班级名称转换器
 *
 * @author R
 * @date 2022-9-29
 */
@Component
public class ClassTransformer implements SimpleTransformer<Long> {

    @Resource
    private ClassService classService;

    @Override
    public String transform(@Nonnull Long classId) {
        String className = classService.getName(classId);
        return Optional.ofNullable(className).orElse(String.valueOf(classId));
    }
}
