package com.robot.translator.translator;

import com.robot.translator.core.annotation.Dictionary;
import com.robot.translator.core.translator.Translatable;

/**
 * 年龄标签翻译，供示例
 *
 * @author luozhan
 * @create 2020-04
 */
public class MyAgeTranslator implements Translatable {
    @Override
    public String translate(String groupValue, String origin, Dictionary dicConfig, Class dictClass) {
        int age = Integer.parseInt(origin);
        if (age < 10) {
            return "小孩";
        } else if (age >= 10 && age < 20) {
            return "少年";
        } else {
            return "壮年";
        }
    }
}
