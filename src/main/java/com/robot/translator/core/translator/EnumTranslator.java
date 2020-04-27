package com.robot.translator.core.translator;

import com.robot.translator.core.annotation.Dictionary;
import com.robot.translator.core.dict.IDict;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.stream.Stream;

/**
 * 枚举翻译
 *
 * @author luozhan
 * @create 2020-04
 */
@Component
public class EnumTranslator implements Translatable {

    @Override
    @SuppressWarnings("unchecked")
    public String translate(String groupValue, String origin, Dictionary dictConfig, Class dictClass) {
        Assert.isTrue(IDict.class.isAssignableFrom(dictClass), dictClass.getSimpleName() + "不是IDictEnum的实现类，无法使用EnumTranslator进行翻译");
        return Stream.of(((Class<IDict>) dictClass).getEnumConstants())
                .filter((IDict e) -> e.getCode().equals(origin))
                .map(IDict::getText)
                .findAny().orElse(null);
    }

}
