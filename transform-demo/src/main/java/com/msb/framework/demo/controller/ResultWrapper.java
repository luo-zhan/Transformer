package com.msb.framework.demo.controller;

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


    public ResultWrapper(String code, String message, T data) {
        this(code, message);
        this.data = data;
    }

    public static <T> ResultWrapper<T> success(T data) {
        ResultWrapper<T> objectDataResult = new ResultWrapper<>("200", "success");
        objectDataResult.setData(data);
        return objectDataResult;
    }

    public static <T> ResultWrapper<T> success() {
        return success(null);
    }


    public static <T> ResultWrapper<T> fail(String code, String message) {
        return new ResultWrapper<>(code, message, null);
    }


}
