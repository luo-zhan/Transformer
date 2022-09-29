package com.robot.transform.component;

import lombok.Data;

/**
 * 字典bean
 * 只有code和text，可用于展示下拉框
 *
 * @author R
 */
@Data
public class DictBean<T> implements Dict<T> {
    private final T code;
    private final String text;
}