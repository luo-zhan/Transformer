package com.robot.transform.demo.controller.transform;

import com.robot.transform.demo.service.DictionaryService;
import com.robot.transform.transformer.IDictTransformer;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 数据字典转换器（示例）
 * 该类需要业务自己实现，注意类名必须为DictTransformer，并使用@Component加入容器
 *
 * @author R
 */
@Component
public class DictTransformer implements IDictTransformer<Integer> {
    @Resource
    private DictionaryService dictionaryService;

    @Override
    public String transform(@NonNull Integer dictCode, String group) {
        // 实际情况这里可以调用远程服务或本地db查询
        return dictionaryService.getText(group, dictCode);
    }
}
