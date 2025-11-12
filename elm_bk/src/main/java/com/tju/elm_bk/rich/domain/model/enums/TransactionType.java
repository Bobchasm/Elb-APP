package com.tju.elm_bk.rich.domain.model.enums;

import java.util.Objects;

public enum TransactionType {

    PAYMENT(0,"支付"),
    COLLECTION(1,"收款"),
    WITHDRAWAL(2,"提现"),
    RECHARGE(3,"充值"),
    REFUND(4, "退款");
    ;


    private Integer type;
    private String description;


    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    TransactionType(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public static TransactionType fromCode(Integer type) {
        for(TransactionType transactionType : TransactionType.values()) {
            if (Objects.equals(transactionType.type, type)) {
                return transactionType;
            }
        }
        return null;
    }
}
