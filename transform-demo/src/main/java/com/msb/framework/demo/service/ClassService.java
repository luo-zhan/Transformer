package com.msb.framework.demo.service;

import org.springframework.stereotype.Service;

/**
 * 班级示例服务
 *
 * @author R
 * @date 2022-08
 */
@Service
public class ClassService {

    public String getName(Long classId) {
        return "三年二班";
    }
}
