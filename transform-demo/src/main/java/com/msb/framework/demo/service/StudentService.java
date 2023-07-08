package com.msb.framework.demo.service;

import com.msb.framework.demo.bean.Student;
import com.msb.framework.demo.enums.Sex;
import org.apache.commons.lang3.RandomUtils;
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
        // 班级id，随机生成
        student.setClassId(RandomUtils.nextLong(1, 10));
        // 性别，随机生成
        student.setSex(RandomUtils.nextBoolean() ? Sex.MALE.getCode() : Sex.FEMALE.getCode());
        // 爱好，随机生成
        student.setHobby(RandomUtils.nextInt(0, 4));
        return student;
    }


}
