package com.tju.elm.payment.domain.model;

import exception.APIException;

import java.math.BigDecimal;

public class VipInfo {
    private final Integer level;
    private final BigDecimal cost;
    private final BigDecimal overdraftLimit;

    public BigDecimal getOverdraftLimit() {
        return overdraftLimit;
    }

    public VipInfo(Integer level, BigDecimal cost, BigDecimal overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
        if (level == null) {
            throw new APIException("VIP等级不能为null");
        }
        this.level = level;
        this.cost = cost;
    }


    public boolean canUpgradeTo(VipInfo targetLevel) {
        return targetLevel.getLevel() > this.level;
    }

    public BigDecimal getCost() {
        return cost;
    }

    public Integer getLevel() {
        return level;
    }
}
