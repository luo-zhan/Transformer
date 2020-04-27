package com.robot.translator.core.dict;

import java.util.stream.Stream;

/**
 * 字典接口
 * <p>
 * 自定义的字典枚举类实现本接口后在构造方法中只需调用init方法即可初始化
 *
 * @author luozhan
 * @date 2019-03
 */
public interface IDict {
    /**
     * 通过code获取value
     *
     * @param clazz 枚举class
     * @param code  code
     * @return
     */
    static String getTextByCode(Class<? extends IDict> clazz, String code) {
        return Stream.of(clazz.getEnumConstants())
                .filter((IDict e) -> e.getCode().equals(code))
                .map(IDict::getText)
                .findAny().orElse(null);
    }

    /**
     * 通过text获取code
     *
     * @param clazz 枚举class
     * @param text  text
     * @return
     */
    static String getCodeByText(Class<? extends IDict> clazz, String text) {
        return Stream.of(clazz.getEnumConstants())
                .filter((IDict e) -> e.getText().equals(text))
                .map(IDict::getCode)
                .findAny().orElse(null);
    }

    /**
     * 初始化
     *
     * @param code 字典编码
     * @param text 字典文本
     */
    default void init(String code, String text) {
        DictPool.putDict(this, code, text);
    }

    /**
     * 获取code
     */
    default String getCode() {
        return DictPool.getDict(this).getCode();
    }

    /**
     * 获取text
     */
    default String getText() {
        return DictPool.getDict(this).getText();
    }
}
