package com.robot.transform.extend.converter;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.core.convert.converter.Converter;

import java.util.List;

/**
 * Page转List
 *
 * @author R
 * @date 2022-9-27
 */
public class PageToListConverter<T> implements Converter<IPage<T>, List<T>> {

    @Override
    public List<T> convert(IPage<T> source) {
        return source.getRecords();
    }
}
