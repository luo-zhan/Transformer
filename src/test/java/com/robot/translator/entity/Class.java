package com.robot.translator.entity;

import com.robot.translator.core.annotation.Dictionary;
import lombok.Data;

import java.io.Serializable;

/**
 * (Class)班级
 *
 */
@Data
@Dictionary(codeColumn = "id", textColumn = "name")
public class Class implements Serializable {
    private static final long serialVersionUID = -40477702922137712L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 班级名称
     */
    private String name;

}