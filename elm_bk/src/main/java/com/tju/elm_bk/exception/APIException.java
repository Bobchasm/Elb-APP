package com.tju.elm_bk.exception;

import com.tju.elm_bk.result.ResultCodeEnum;
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
    public APIException(ResultCodeEnum resultCode) {
        super(resultCode.getMessage());
        this.code = Integer.valueOf(resultCode.getCode());
    }
}
