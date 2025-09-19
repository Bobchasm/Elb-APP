package com.tju.elm_bk.result;

public enum ResultCodeEnum {
    /*** 通用部分 100 - 599***/
    // 成功请求
    SUCCESS("OK", "successful"),
    // 资源未找到
    NOT_FOUND("NOT_FOUND", "not found"),
    // 服务器错误
    SERVER_ERROR("GENERAL_ERROR","server error"),

    COMMON_ERROR("COMMON_ERROR","通用返回失败"),

    CLIENT_ABORT("CLIENT_ABORT","异常中止"),
    PARAM_NOT_MATCHED_GET("PARAM_NOT_MATCHED_GET","GET参数有误"),
    PARAM_NOT_MATCHED_POST("PARAM_NOT_MATCHED_POST","POST参数有误"),
    PARAM_NOT_MATCHED("PARAM_NOT_MATCHED","参数不匹配"),
    NOT_SUPPORTED("NOT_SUPPORTED","请求方式不支持"),
    NOT_KNOWN_ERROR("NOT_KNOWN_ERROR","未知错误"),
    PARAM_VERIFIED_FAILED("PARAM_VERIFIED_FAILED","参数校验不对"),
    WITHOUT_ERROR_CODE("WITHOUT_ERROR_CODE","无错误码"),

    ADDRESS_PERMISSION_DENIED("ADDRESS_PERMISSION_DENIED","权限不足，无法为该用户添加地址"),
    NOT_ENOUGH_PERMISSION("NOT_ENOUGH_PERMISSION","权限不足"),
    VALUE_MISSED("VALUE_MISSED","值不存在"),
    FOOD_MISSED("FOOD_MISSED","商品不存在"),


    ORDER_MISSED("ORDER_MISSED","订单不存在"),
    ORDER_STATUS_UNMATCHED("ORDER_STATUS_UNMATCHED","订单状态错误"),
    ORDER_PAY_FAILED("ORDER_PAY_FAILED","订单支付失败"),
    ORDER_ACCEPT_FAILED("ORDER_ACCEPT_FAILED","接单失败"),
    ORDER_CANCEL_DENY("ORDER_CANCEL_DENY","无法取消已被接收的订单"),
    ORDER_CANCEL_FAILED("ORDER_CANCEL_FAILED","订单取消失败"),

    QUANTITY_ILLEGAL("QUANTITY_ILLEGAL","数量不合法"),

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
