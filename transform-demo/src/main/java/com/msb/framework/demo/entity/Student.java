package com.msb.framework.demo.entity;

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
    // 班干部-枚举（1-班长，2-组长，3-学习委员，0-普通成员）
    private Integer classLeader;
    // 性别-数据字典表（0-男，1-女）
    private Integer sex;
    // 班级-需班级表翻译（class表根据id查询name）
    private Long classId;
}
