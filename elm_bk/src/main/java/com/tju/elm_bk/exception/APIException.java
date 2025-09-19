package com.tju.elm_bk.exception;

import lombok.Getter;

// 接口错误时统一抛出APIException 由全局异常处理器统一捕获
@Getter
public class APIException extends RuntimeException {
    private Integer code;
    public APIException(String message) {
        super(message);
    }

    public APIException(int code, String message) {
        super(message);
        this.code = code;
    }
}
