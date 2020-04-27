package com.robot.translator.dict;

import com.robot.translator.core.dict.IDict;

/**
 * 枚举值常量
 * 真实应用中，可以将同一个业务的字典枚举放在一个接口中方便维护
 *
 * @author luozhan
 * @date 2019-03
 */

public interface MyDict {
    /**
     * 示例1：性别枚举
     */
    enum SexDict implements IDict {
        MALE("0", "男"),
        FEMALE("1", "女");

        SexDict(String code, String text) {
            // 构造方法中只需要调用接口的init方法即可，省略了属性的定义和赋值，也不用定义累赘的get方法
            init(code, text);
        }
    }

    /**
     * 示例2：是、否枚举
     */
    enum YesNoDict implements IDict {
        YES("1", "是"),
        NO("0", "否");

        YesNoDict(String code, String text) {
            init(code, text);
        }
    }

}
