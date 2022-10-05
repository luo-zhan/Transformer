package com.msb.framework.demo.service;

import com.msb.framework.demo.bean.Student;
import com.msb.framework.demo.enums.Sex;
import org.springframework.stereotype.Service;

/**
 * 学生服务（示例）
 *
 * @author R
 * @date 2022-08
 */
@Service
public class StudentService {

    /**
     * 根据id查询学生信息
     *
     * @param id 学生id
     */
    public Student getById(Long id) {
        // 模拟返回数据
        // 实际情况应从db中查询
        Student student = new Student();
        student.setId(id);
        student.setName("学生" + id);
        student.setClassId(300L);
        // 男
        student.setSex(Sex.MALE.getCode());
        // 班长
        int monitor = 1;
        student.setClassLeader(monitor);
        return student;
    }


}
