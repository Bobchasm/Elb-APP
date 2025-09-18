package com.tju.elm_bk.result;

public enum ResultCodeEnum {
    /*** 通用部分 100 - 599***/
    // 成功请求
    SUCCESS("OK", "successful"),
    // 资源未找到
    NOT_FOUND("NOT_FOUND", "not found"),
    // 服务器错误
    SERVER_ERROR("GENERAL_ERROR","server error"),

    CLIENT_ABORT("CLIENT_ABORT","异常中止"),
    PARAM_NOT_MATCHED_GET("PARAM_NOT_MATCHED_GET","GET参数有误"),
    PARAM_NOT_MATCHED_POST("PARAM_NOT_MATCHED_POST","POST参数有误"),
    PARAM_NOT_MATCHED("PARAM_NOT_MATCHED","参数不匹配"),
    NOT_SUPPORTED("NOT_SUPPORTED","请求方式不支持"),
    NOT_KNOWN_ERROR("NOT_KNOWN_ERROR","未知错误"),
    PARAM_VERIFIED_FAILED("PARAM_VERIFIED_FAILED","参数校验不对"),

    ADDRESS_PERMISSION_DENIED("ADDRESS_PERMISSION_DENIED","权限不足，无法为该用户添加地址"),
    /*** 这里可以根据不同模块用不同的区级分开错误码，例如:  ***/

    // 1000～1999 区间表示用户模块错误
    // 2000～2999 区间表示订单模块错误
    // 3000～3999 区间表示商品模块错误
    // 。。。

    ;
    /**
     * 响应状态码
     */
    private String code;
    /**
     * 响应信息
     */
    private String message;

    ResultCodeEnum(String code, String msg) {
        this.code = code;
        this.message = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
