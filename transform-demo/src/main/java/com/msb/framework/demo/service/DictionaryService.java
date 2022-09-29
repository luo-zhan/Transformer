package com.msb.framework.demo.service;

import org.springframework.stereotype.Service;

/**
 * @author R
 * @date 2022-08
 */
@Service
public class DictionaryService {

    public String getText(String dictionaryType, Integer code) {
        if ("sex".equals(dictionaryType)) {
            return 1 == code ? "男" : "女";
        }
        return null;
    }
}
