package com.robot.translator.core.dict;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 字典池
 *
 * @author luozhan
 * @create 2020-04
 */
class DictPool {
    private static final Map<IDict, DictBean> DICT_MAP = new ConcurrentHashMap<>();

    static void putDict(IDict dict, String code, String text) {
        DICT_MAP.put(dict, new DictBean(code, text));
    }

    static DictBean getDict(IDict dict) {
        return DICT_MAP.get(dict);
    }

    static class DictBean implements IDict {
        private String code;
        private String text;

        DictBean(String code, String text) {
            this.code = code;
            this.text = text;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
