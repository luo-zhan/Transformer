package com.msb.framework.demo.bean;

import com.msb.framework.demo.controller.transform.TransformClass;
import com.msb.framework.demo.enums.Sex;
import com.robot.transform.annotation.Transform;
import com.robot.transform.annotation.TransformDict;
import com.robot.transform.annotation.TransformEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 学生VO
 *
 * @author R
 * @date 2022-08
 */
@Data
@SuppressWarnings("all")
public class StudentVO {
    private Long id;
    // 名字
    private String name;

    // 班干部id
    private Integer classLeader;

    // 班干部名称（数据字典转换）
    @TransformDict(group = "classLeader")
    private String classLeaderName;

    // 性别-枚举（0-男，1-女）
    private Integer sex;

    // 性别名称（枚举转换）
    @TransformEnum(Sex.class)
    private String sexName;

    // 班级id
    private Long classId;

    // 班级名称（自定义转换：根据班级id查询name）
    @TransformClass
    // 下面这种方式为原始写法，可简化成上面的方式
    // @Transform(from = "classId", transformer = ClassTransformer.class)
    private String className;

    // 小组成员（嵌套转换）
    @Transform
    private List<StudentVO> team = new ArrayList<>();

    // 同桌（嵌套转换）
    @Transform
    private StudentVO deskmate;

}
