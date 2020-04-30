package com.robot.translator.core.translator;

import com.robot.translator.core.annotation.Dictionary;

/**
 * 字典接口
 * <p>
 * 实现本接口后，可在@Dictionary注解的method属性中指定实现类，以实现特殊的翻译
 *
 * @author luozhan
 * @create 2020-04
 */
public interface Translatable {

    /**
     * 自定义翻译方法，需要自己实现
     *
     * @param groupValue 字典group值，不用可忽略（如静态字典需要先通过group值确定范围，再根据code值得到对应的value）
     * @param origin     待翻译的原始值（对应字典code属性）
     * @param dictConfig  字典注解，可获取属性配置
     * @param dictClass  字典class
     * @return 字典value，可以返回null值，翻译时会处理（如果为null则显示原始值）
     */
    String translate(String groupValue, String origin, Dictionary dictConfig, Class dictClass);

}
