package com.robot.transform.component;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;


/**
 * 解包器
 * <p>
 * 当方法返回值是包装类（如Page、ResultWrapper等）时，指定解包的逻辑
 * 注意解包之后的返回参数必须是某个bean或者集合类型
 *
 * @author R
 */
public interface UnWrapper<T> extends Converter<T, Object> {

    /**
     * 解包
     *
     * @param source 源
     * @return 包装类内的实际对象
     */
    Object unWrap(T source);

    /**
     * 将convert更名为unWrap
     *
     * @param source 源
     * @return 目标对象
     */
    @Override
    default Object convert(@NonNull T source) {
        return unWrap(source);
    }
}
