package com.robot.translator.entity;

import com.robot.translator.core.annotation.Dictionary;
import com.robot.translator.core.annotation.Translate;
import com.robot.translator.translator.MyAgeTranslator;
import lombok.Data;

import java.io.Serializable;

/**
 * (Student)学生
 */
@Data
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
    // 静态字典翻译和枚举翻译，都可以支持
    // @Translate(dictClass = StaticDict.class, groupValue = "sex")
    @Translate(dictClass = StaticDict.class, groupValue = "sex")
    private String sex;
    private String sexName;
    /**
     * 年龄
     */
    @Translate(dictionary = @Dictionary(translator = MyAgeTranslator.class), translateField = "tag")
    private Integer age;
    /**
     * 年龄标签，由年龄决定
     */
    private String tag;


}