package com.robot.transform.demo.controller.transform;

import com.robot.transform.demo.bean.ClassDTO;
import com.robot.transform.demo.service.ClassService;
import com.robot.transform.transformer.BatchTransformer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 班级名称转换器
 *
 * @author R
 * @since 2022-9-29
 */
@Component
public class ClassTransformer implements BatchTransformer<Long, TransformClass> {

    @Resource
    private ClassService classService;


    @NonNull
    @Override
    public Map<Long, String> batchTransform(@NonNull Set<Long> classIds) {
        // 批量查询
        List<ClassDTO> classList = classService.queryClassInfo(classIds);
        return classList.stream().collect(Collectors.toMap(ClassDTO::getId, ClassDTO::getName));
    }


    @Override
    public String transform(@NonNull Long originalValue) {
        return classService.getName(originalValue);
    }
}
