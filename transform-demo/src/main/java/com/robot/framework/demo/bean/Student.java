package com.robot.framework.demo.bean;

import lombok.Data;

/**
 * 学生bean
 *
 * @author R
 * @date 2022-08
 */
@Data
@SuppressWarnings("all")
public class Student {
    private Long id;
    // 名字
    private String name;
    // 性别-枚举（0-男，1-女）
    private Integer sex;
    // 爱好-数据字典（1-学习，2-音乐，3-运动，0-无爱好）
    private Integer hobby;
    // 班级-需班级表翻译（class表根据id查询name）
    private Long classId;
}
