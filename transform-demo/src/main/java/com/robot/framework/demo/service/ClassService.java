package com.robot.framework.demo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 班级服务
 *
 * @author R
 * @date 2022-08
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
}
