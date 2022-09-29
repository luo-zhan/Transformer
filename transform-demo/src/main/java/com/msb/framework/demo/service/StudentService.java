package com.msb.framework.demo.service;

import com.msb.framework.demo.entity.Student;
import com.msb.framework.demo.entity.StudentVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 学生示例服务
 *
 * @author R
 * @date 2022-08
 */
@Service
public class StudentService {
    @Resource
    private DictionaryService dictionaryService;

    public Student getById(Long id) {
        int male = 1;
        Student student = new Student();
        student.setId(id);
        student.setName("周杰伦");
        student.setClassId(300L);
        // 男
        student.setSex(male);
        // 班长
        student.setClassLeader(StudentVO.ClassLeaderEnum.MONITOR.getCode());
        return student;
    }
}
