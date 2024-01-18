package com.robot.transform.extend.unwrapper;


import com.mybatisflex.core.paginate.Page;
import com.robot.transform.component.UnWrapper;


/**
 * mybatis-plus的IPage解包器
 *
 * @author R
 * @since 2022-9-27
 */
public class PageUnWrapper<T> implements UnWrapper<Page<T>> {

    @Override
    public Object unWrap(Page<T> source) {
        return source.getRecords();
    }

}
