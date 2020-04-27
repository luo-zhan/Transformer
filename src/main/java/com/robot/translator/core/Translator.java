package com.robot.translator.core;

import com.robot.translator.core.annotation.Dictionary;
import com.robot.translator.core.annotation.Translate;
import com.robot.translator.core.dict.IDict;
import com.robot.translator.core.enums.FormatType;
import com.robot.translator.core.translator.DefaultTranslator;
import com.robot.translator.core.translator.EnumTranslator;
import com.robot.translator.core.translator.Translatable;
import com.robot.translator.core.util.SpringContextUtil;
import com.robot.translator.core.util.StringUtil;
import org.springframework.stereotype.Component;
import sun.reflect.annotation.AnnotationType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.robot.translator.core.util.LambdaUtil.uncheck;

/**
 * 翻译工具
 * 结合注解使用
 *
 * @author luozhan
 * @see Translate
 * @see Dictionary
 */
@Component
public class Translator {

    /**
     * 翻译Map或Entity或Page
     *
     * @param origin 需要翻译的数据
     * @param clazz  指定翻译的模板class，可传多个
     * @param <T>    Map或Entity
     * @return origin
     */
    public static <T> T parse(T origin, Class<?>... clazz) {
        if (origin == null) {
            return null;
        }
        return parse(Collections.singletonList(origin), clazz).get(0);
    }

    /**
     * 翻译集合
     * 集合元素为Map或Entity
     * <p>
     * 注意：
     * 如果不指定class，默认使用List中元素的class上配置的翻译规则进行翻译
     * 当List集合元素不为Entity类型时，class参数至少需要指定1个
     *
     * @param origins 待翻译数据集合
     * @param classes 配置了翻译规则的Entity类型，可传多个
     * @param <T>     支持Entity或者Map
     * @return List
     */
    public static <T> List<T> parse(List<T> origins, Class<?>... classes) {
        if (origins.size() == 0) {
            return origins;
        }
        classes = (classes.length != 0) ? classes : new Class[]{origins.get(0).getClass()};

        // 获取bo中需要翻译的属性
        List<Field> translateFieldList = Arrays.stream(classes)
                .map(Class::getDeclaredFields)
                .flatMap(Stream::of)
                .filter(field -> field.isAnnotationPresent(Translate.class))
                .collect(Collectors.toList());
        // 源数据中属性的格式（大写下划线，小写下划线，驼峰）
        FormatType fieldFormatType = getFieldType(origins);

        for (Field field : translateFieldList) {
            // 1.获取要翻译的属性名
            String fieldName = StringUtil.parseCamelTo(field.getName(), fieldFormatType);

            // 2.获取每个待翻译属性的配置
            Translate translateConfig = field.getAnnotation(Translate.class);
            // 配置的字典class
            Class<?> dictClass = getDictClass(translateConfig);
            // if (dictClass == void.class) {
            //     throw new IllegalArgumentException(String.format("属性名%s上的@Translate注解找不到dictClass或value配置", fieldName));
            // }

            // 获取翻译值写入的字段名
            String translateField = StringUtil.parseCamelTo(getTranslateFieldName(translateConfig, field.getName()), fieldFormatType);

            // 字典组字段值
            String groupValue = translateConfig.groupValue();
            // 字典注解配置
            Dictionary dictionaryConfig = handle(translateConfig);

            for (T origin : origins) {
                Object originValue = getProperty(origin, fieldName);
                if (originValue == null) {
                    continue;
                }
                // 翻译
                String translateValue = parse(String.valueOf(originValue), dictionaryConfig, dictClass, groupValue);
                // 填值
                setProperty(origin, translateField, translateValue);
            }
        }
        return origins;
    }

    /**
     * 判断源数据中的属性格式类型
     */
    @SuppressWarnings("unchecked")
    private static <T> FormatType getFieldType(List<T> origins) {
        T element = origins.get(0);
        if (Map.class.isAssignableFrom(element.getClass())) {
            Set<String> keySet = ((Map) element).keySet();
            for (String key : keySet) {
                if (key.toUpperCase().equals(key)) {
                    return FormatType.UPPERCASE_UNDERLINE;
                } else if (key.contains("_")) {
                    return FormatType.LOWERCASE_UNDERLINE;
                }
            }
        }
        return FormatType.CAMEL;

    }

    private static Class<?> getDictClass(Translate translateConfig) {
        return translateConfig.dictClass() == void.class ? translateConfig.value() : translateConfig.dictClass();
    }

    /**
     * 将dictClass类上的Dictionary的配置填充到Translate注解中的dictionary属性中
     * 此步骤将合并两个注解中的配置，且Translate注解中的配置优先级更高
     */
    private static Dictionary handle(Translate translateConfig) {
        Class<?> dictClass = getDictClass(translateConfig);
        Dictionary dictionaryConfigOnDictClass = dictClass.getAnnotation(Dictionary.class);
        Dictionary dictionaryConfigInTranslateConfig = translateConfig.dictionary();
        return (Dictionary) joinAnnotationValue(dictionaryConfigOnDictClass, dictionaryConfigInTranslateConfig);
    }

    /**
     * 将注解属性填充到另一个相同类型的注解中，目标注解中已经存在属性值的不会被覆盖
     *
     * @param annotationFrom
     * @param annotationTo
     * @return 返回annotationTo，如果annotationTo为空，返回annotationFrom
     */
    @SuppressWarnings("all")
    private static Annotation joinAnnotationValue(Annotation annotationFrom, Annotation annotationTo) {
        if (annotationTo == null) {
            return annotationFrom;
        }
        if (annotationFrom == null) {
            return annotationTo;
        }
        Object handlerFrom = Proxy.getInvocationHandler(annotationFrom);
        Object handlerTo = Proxy.getInvocationHandler(annotationTo);

        Field fieldFrom = uncheck(() -> handlerFrom.getClass().getDeclaredField("memberValues"));
        Field fieldTo = uncheck(() -> handlerTo.getClass().getDeclaredField("memberValues"));

        fieldFrom.setAccessible(true);
        fieldTo.setAccessible(true);

        Map<String, Object> memberValuesFrom = uncheck(() -> (Map) fieldFrom.get(handlerFrom));
        Map<String, Object> memberValuesTo = uncheck(() -> (Map) fieldTo.get(handlerTo));

        // 注解默认值，注意不会包含没有默认值的属性
        Map<String, Object> defaultValueMap = AnnotationType.getInstance(annotationTo.annotationType()).memberDefaults();
        // 若目标注解中全都是默认值（代表没有设置），则直接返回原注解
        // 否则属性填充后会直接改变目标注解的默认值，影响其他引用的地方
        if (!defaultValueMap.equals(memberValuesTo)) {
            return annotationFrom;
        }
        // 如果目标注解属性未设置，则往目标里填充值
        memberValuesTo.forEach((field, value) -> {
            if (value.equals(defaultValueMap.get(field))) {
                memberValuesTo.put(field, memberValuesFrom.get(field));
            }
        });

        return annotationTo;
    }


    /**
     * 翻译单值
     *
     * @param originValue 原始值
     * @param dictConfig  字典配置
     * @param dictClass   字典class（包含组别属性、字典code属性、字典值属性三个信息）
     * @param groupValue  组别的值，由使用者指定
     * @return 翻译后的值，如果字典中找不到翻译值返回原始值
     */
    @SuppressWarnings("all")
    public static String parse(String originValue, Dictionary dictConfig, Class<?> dictClass, String groupValue) {
        if (originValue == null) {
            return null;
        }
        Class<? extends Translatable> translatorClass = dictConfig.translator();
        if (translatorClass == Translatable.class) {
            // 未指定translator，采用默认配置：
            // todo：默认翻译类支持配置化
            if (IDict.class.isAssignableFrom(dictClass)) {
                // 1.dictClass是枚举类，采用枚举翻译
                translatorClass = EnumTranslator.class;
            } else {
                // 2.否则使用默认翻译
                translatorClass = DefaultTranslator.class;
            }
        }

        // 调用翻译方法
        Translatable translator;
        if (translatorClass.isAnnotationPresent(Component.class)) {
            // 实现类上配置了@Component则使用Spring容器获取
            translator = SpringContextUtil.getBean(translatorClass);
        } else {
            translator = uncheck(translatorClass::newInstance);
        }
        String translateResult = translator.translate(groupValue, originValue, dictConfig, dictClass);
        return translateResult == null ? originValue : translateResult;
    }

    /**
     * 若注解中未配置translateField，则默认将原属性名的Id或Code字样替换成Name
     * <p>
     * 如：
     * resTypeId -> resTypeCode
     * staff -> staffName
     */
    private static String getTranslateFieldName(Translate translateConfig, String originFieldName) {
        String translateField = translateConfig.translateField();
        if ("".equals(translateField)) {
            translateField = originFieldName.replaceFirst("(Id|Code)$|$", "Name");
        }
        return translateField;
    }


    private static Object getProperty(Object o, String fieldName) {
        if (o instanceof Map) {
            return ((Map) o).get(fieldName);
        } else {
            Method getMethod = getMethod(o.getClass(), fieldName, "get");
            // 此处不会抛异常
            return uncheck(() -> getMethod.invoke(o));
        }
    }

    @SuppressWarnings("unchecked")
    private static void setProperty(Object o, String fieldName, String value) {
        if (o instanceof Map) {
            ((Map) o).put(fieldName, value);
        } else {
            Method setMethod = getMethod(o.getClass(), fieldName, "set");
            // 此处不会抛异常
            uncheck(() -> setMethod.invoke(o, value));
        }
    }

    /**
     * 通过方法名找到get或set方法
     * 只是简单根据名称匹配，要求clazz是个常规的bean
     */
    private static Method getMethod(Class<?> clazz, String fieldName, String prefix) {
        String methodName = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        return Stream.of(clazz.getDeclaredMethods())
                .filter(method -> method.getName().equals(methodName))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException(clazz.getSimpleName() + ".class中未添加翻译属性" + fieldName + "或其对应get/set方法"));
    }

}

