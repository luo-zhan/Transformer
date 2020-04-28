package com.robot.translator.entity;

import com.robot.translator.core.annotation.Dictionary;
import lombok.Data;

/**
 * (StaticDict)静态数据字典
 *
 * @author luozhan@asiainfo.com
 * @since 2020-04-27 10:25:42
 */
@Data
@Dictionary(codeColumn = "dict_code", textColumn = "dict_text", groupColumn = "group_code")
public class StaticDict {
    private Long id;
    private String group;
    private String dictCode;
    private String dictText;
}