package com.tju.elm.payment.domain.model;


import exception.APIException;

import java.math.BigDecimal;

public class Balance {
    public static final Balance ZERO = new Balance(BigDecimal.ZERO);

    private final BigDecimal amount;

    public Balance(BigDecimal amount) {
        if (amount == null) {
            throw new APIException("金额不能为空");
        }
        if (amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new APIException("余额不能为负数: " + amount);
        }
        // 统一精度：2位小数，四舍五入
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public Balance add(BigDecimal other) {
        if (other == null || other.compareTo(BigDecimal.ZERO) <= 0) {
            throw new APIException("增加金额必须大于0");
        }
        return new Balance(this.amount.add(other));
    }

    public Balance subtract(BigDecimal other) {
        if (other == null || other.compareTo(BigDecimal.ZERO) <= 0) {
            throw new APIException("减少金额必须大于0");
        }

        BigDecimal result = this.amount.subtract(other);
        if (result.compareTo(BigDecimal.ZERO) < 0) {
            throw new APIException("余额不足，当前余额: " + this.amount);
        }
        return new Balance(result);
    }

    public boolean canAfford(BigDecimal amount) {
        return this.amount.compareTo(amount) >= 0;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return amount.compareTo(balance.amount) == 0;
    }

    @Override
    public int hashCode() {
        return amount.hashCode();
    }

    @Override
    public String toString() {
        return "Balance{amount=" + amount + '}';
    }
}
