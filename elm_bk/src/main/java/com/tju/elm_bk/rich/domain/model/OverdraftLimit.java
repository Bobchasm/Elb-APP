package com.tju.elm_bk.rich.domain.model;

import com.tju.elm_bk.exception.APIException;

import java.math.BigDecimal;

public class OverdraftLimit {
    public static final OverdraftLimit ZERO = new OverdraftLimit(BigDecimal.ZERO);

    private final BigDecimal amount;

    public OverdraftLimit(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new APIException("透支额度不能为负数");
        }
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean hasOverdraft() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
