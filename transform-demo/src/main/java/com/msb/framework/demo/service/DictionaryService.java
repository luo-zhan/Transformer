package com.msb.framework.demo.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据字典服务（示例）
 * 这里是写死的数据，实际情况应该是db中查询
 *
 * @author R
 * @date 2022-08
 */
@Service
public class DictionaryService {
    private static final Map<Integer, String> MAP = new HashMap<>();

    static {
        MAP.put(0, "普通成员");
        MAP.put(1, "班长");
        MAP.put(2, "副班长");
        MAP.put(3, "学习委员");
    }

    /**
     * 根据分组和code获取文本
     *
     * @param group 分组
     * @param code code
     * @return 文本
     */
    public String getText(String group, Integer code) {
        // 班干部字典，模拟从db中查询
        String classLeader = "classLeader";
        if (classLeader.equals(group)) {
            return MAP.get(code);
        }
        return null;
    }
}
