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
        MAP.put(0, "无爱好");
        MAP.put(1, "学习");
        MAP.put(2, "音乐");
        MAP.put(3, "运动");
    }

    /**
     * 根据分组和code获取文本
     *
     * @param group    分组
     * @param dictCode 字典code
     * @return 文本
     */
    public String getText(String group, Integer dictCode) {
        // 此处模拟从数据字典表中查询
        String groupName = "hobby";
        if (groupName.equals(group)) {
            return MAP.get(dictCode);
        }
        return null;
    }
}
