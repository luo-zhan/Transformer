package com.msb.framework.demo.entity;

import com.msb.framework.demo.controller.transform.TransformClass;
import com.robot.transform.annotation.Transform;
import com.robot.transform.annotation.TransformDict;
import com.robot.transform.annotation.TransformEnum;
import com.robot.transform.component.Dict;
import lombok.Data;

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

    // 班干部-枚举（1-班长，2-副班长，3-学习委员，0-普通成员）
    private Integer classLeader;

    //  @Transform(transformer = EnumTransformer.class)
//    @Transform(from = "classLeader", transformer = EnumTransformer.class)
    @TransformEnum(ClassLeaderEnum.class)
    private String classLeaderName;

    // 性别-数据字典表（0-男，1-女）
    private Integer sex;
    @TransformDict(group = "sex")
    private String sexName;

    // 班级-需通过班级信息进行转换（class表根据id查询name）
    private Long classId;
    @TransformClass
    // @Transform(from = "classId", transformer = ClassTransformer.class)
    private String className;
    @Transform
    private List<StudentVO> team;
    @Transform
    private StudentVO partner;


    public static enum ClassLeaderEnum implements Dict<Integer> {

        MONITOR(1, "班长"),
        VICE_MONITOR(2, "副班长"),
        STUDY(3, "学习委员"),
        NORMAL(0, "普通成员");

        ClassLeaderEnum(Integer code, String text) {
            init(code, text);
        }
    }
}
