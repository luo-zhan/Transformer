package com.robot.translator.entity;

import com.robot.translator.core.annotation.Translate;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * (Student)学生
 *
 * @author luozhan@asiainfo.com
 * @since 2020-04-27 10:24:38
 */
@Data
@NoArgsConstructor
public class Student implements Serializable {
    private static final long serialVersionUID = -91472499120112751L;
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 姓名
     */
    private String name;
    /**
     * 班级
     */
    @Translate(Class.class)
    private String classId;
    private String className;
    /**
     * 性别
     */
    @Translate(dictClass = StaticDict.class, groupValue = "sex")
    private String sex;
    private String sexName;
    /**
     * 年龄
     */
    private Integer age;


}