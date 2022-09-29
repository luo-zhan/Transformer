package com.msb.framework.demo.controller.transform;

import com.robot.transform.transformer.IDictTransformer;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;

/**
 * 数据字典示例
 *
 * @author R
 */
@Component
public class DictTransformer implements IDictTransformer<Integer> {
    @Override
    public String transform(@Nonnull Integer originalValue, String group) {
        // 实际情况这里可以调用远程服务或本地服务
        return "男";
    }
}
