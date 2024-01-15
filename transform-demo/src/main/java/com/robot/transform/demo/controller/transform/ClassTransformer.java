package com.robot.transform.demo.controller.transform;

import com.robot.transform.demo.service.ClassService;
import com.robot.transform.transformer.SimpleTransformer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * 班级名称转换器
 *
 * @author R
 * @since 2022-9-29
 */
@Component
public class ClassTransformer implements SimpleTransformer<Long> {

    @Resource
    private ClassService classService;

    @Override
    public String transform(@NonNull Long classId) {
        String className = classService.getName(classId);
        return Optional.ofNullable(className).orElse(String.valueOf(classId));
    }
}
