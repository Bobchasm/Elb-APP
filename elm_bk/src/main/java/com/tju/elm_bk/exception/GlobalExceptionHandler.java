package com.tju.elm_bk.exception;

import com.tju.elm_bk.constant.MessageConstant;
import com.tju.elm_bk.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler
    public void clientAbortExceptionHandler(ClientAbortException clientAbortException) {
        log.warn(MessageConstant.CLIENT_ABORT);
    }

    // query参数不全
    @ExceptionHandler
    public Result<Object> apiExceptionHandler(MissingServletRequestParameterException paramException) {
        return Result.fail(MessageConstant.PARAM_NOT_MATCHED_GET);
    }
    // body参数不全
    @ExceptionHandler
    public Result<Object> apiExceptionHandler(HttpMessageNotReadableException messageNotReadableException) {
        return Result.fail(MessageConstant.PARAM_NOT_MATCHED_POST);
    }
    // 参数不匹配
    @ExceptionHandler
    public Result<Object> apiExceptionHandler(MethodArgumentTypeMismatchException methodArgumentTypeMismatchException) {
        log.error(methodArgumentTypeMismatchException.getMessage());
        return Result.fail(MessageConstant.PARAM_NOT_MATCHED);
    }
    // 请求类型不支持
    @ExceptionHandler
    public Result<Object> apiExceptionHandler(HttpRequestMethodNotSupportedException httpRequestMethodNotSupportedException) {
        return Result.fail(MessageConstant.NOT_SUPPORTED);
    }
    // 参数校验不对
    @ExceptionHandler
    public Result<Object> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return Result.fail(errors.toString());
    }

    /**
     * 捕获业务逻辑异常
     * @param ex 异常
     */
    @ExceptionHandler
    public Result<Object> exceptionHandler(Exception ex) {
        log.error("异常为：{}",ex.getClass());
        log.error("堆栈信息：", ex);
        if (ex.getMessage() == null){//异常信息为空 避免空指针异常
            return Result.fail(MessageConstant.NOT_KNOWN_ERROR);
        }
        // 如果有错误码
        if (ex instanceof APIException) {
            Integer code = ((APIException) ex).getCode();
            if (code != null) {
                return Result.fail(code, ex.getMessage());
            }
        }
        // 其他提示
        if(ex.getMessage().length() > 50){
            return Result.fail(MessageConstant.NOT_KNOWN_ERROR);
        }
        // 无错误码 返回-1和message
        return Result.fail(ex.getMessage());
    }

}
