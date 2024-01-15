package com.robot.transform.extend.unwrapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.robot.transform.component.UnWrapper;


/**
 * mybatis-plus的IPage解包器
 *
 * @author R
 * @since 2022-9-27
 */
public class IPageUnWrapper<T> implements UnWrapper<IPage<T>> {

    @Override
    public Object unWrap(IPage<T> source) {
        return source.getRecords();
    }

}
