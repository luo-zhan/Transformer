package com.robot.translator.entity;

import com.robot.translator.core.annotation.Dictionary;

import java.io.Serializable;

/**
 * (StaticDict)静态数据字典
 *
 * @author luozhan@asiainfo.com
 * @since 2020-04-27 10:25:42
 */
@Dictionary(codeColumn = "dict_code", textColumn = "dict_text", groupColumn = "group_code")
public class StaticDict implements Serializable {
    private static final long serialVersionUID = 837652642519567677L;
    /**
     * 主键ID
     */
    private Long id;
    /**
     * 组名
     */
    private String groupCode;
    /**
     * 字典编码
     */
    private String dictCode;
    /**
     * 字典文本
     */
    private String dictText;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictText() {
        return dictText;
    }

    public void setDictText(String dictText) {
        this.dictText = dictText;
    }

}