package com.robot.translator.core.util;

import com.robot.translator.core.enums.FormatType;

/**
 * @author luozhan
 * @create 2020-04
 */
public class StringUtil {
    public static String parseCamelTo(String param, FormatType type) {
        if (type == FormatType.CAMEL) {
            return param;
        }
        int len = param.length();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char c = param.charAt(i);
            if (Character.isUpperCase(c)) {
                sb.append("_");
            }
            char result = type == FormatType.UPPERCASE_UNDERLINE ?
                    Character.toUpperCase(c) : Character.toLowerCase(c);
            sb.append(result);
        }
        return sb.toString();
    }
}
