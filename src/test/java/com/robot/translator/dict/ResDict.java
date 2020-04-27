package com.robot.translator.dict;

import com.robot.translator.core.dict.IDict;

/**
 * 资源枚举值定义
 *
 * @author luozhan
 * @date 2019-03
 */

public interface ResDict {
    /**
     * "是/否"枚举
     */
    enum YesOrNoDict implements IDict {
        /**
         * 是
         */
        YES("1", "是"),
        /**
         * 否
         */
        NO("0", "否");

        YesOrNoDict(String code, String text) {
            init(code, text);
        }
    }

}
