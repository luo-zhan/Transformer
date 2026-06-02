package com.robot.transform.demo.service;

import com.robot.transform.demo.bean.ClassDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 班级服务
 *
 * @author R
 * @since 2022-08
 */
@Slf4j
@Service
public class ClassService {

    /**
     * 根据班级id查询班级名称
     *
     * @param classId 班级id
     * @return 班级名称
     */
    public String getName(Long classId) {
        // 实际情况下应查询db
        return "三年" + classId + "班";

    }

    public List<ClassDTO> queryClassInfo(Set<Long> ids) {

        // 根据ids响应班级名称
        return ids.stream()
                .map(classId -> new ClassDTO(classId, "三年" + classId + "班"))
                .collect(Collectors.toList());
    }
}
