package com.msb.framework.demo.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * 返回参数包装类
 *
 * @author R
 */
@Setter
@Getter
public class ResultWrapper<T> {
    private String code;
    private String message;
    private T data;


    public ResultWrapper(String code, String message) {
        this.code = code;
        this.message = message;
    }


    public static <T> ResultWrapper<T> success(T data) {
        ResultWrapper<T> objectDataResult = new ResultWrapper<>("200", "success");
        objectDataResult.setData(data);
        return objectDataResult;
    }

}
