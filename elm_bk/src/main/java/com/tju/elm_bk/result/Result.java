package com.tju.elm_bk.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一返回结果
 * code 0-成功
 * code 其他-失败
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {

    private int code;
    private String message;
    private T data;

    public Result(T data) {
        this.code = 0;
        this.message = "success";
        this.data = data;
    }


    public Result(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    public static <T> Result<T> success(T data) {
        return new Result<>(data);
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(-1, message);
    }

    public static <T> Result<T> fail(int code, String message) {
        return new Result<>(code, message);
    }
}

