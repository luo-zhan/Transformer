package com.robot.translator.entity;

import com.robot.translator.core.annotation.Dictionary;

import java.io.Serializable;

/**
 * (Class)班级
 *
 * @author luozhan@asiainfo.com
 * @since 2020-04-27 10:25:42
 */
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}