package com.robot.transform.component;

import lombok.Data;

import java.io.Serializable;

/**
 * 字典bean
 * 只有code和text，可用于展示下拉框
 *
 * @author R
 */
@Data
public class DictBean implements Dict<Serializable> {
    private final Serializable code;
    private final String text;
}