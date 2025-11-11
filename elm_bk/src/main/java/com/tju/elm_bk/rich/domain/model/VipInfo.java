package com.tju.elm_bk.rich.domain.model;

import com.tju.elm_bk.exception.APIException;

import java.math.BigDecimal;

public class VipInfo {
    private final Integer level;
    private final BigDecimal cost;

    public VipInfo(Integer level, BigDecimal cost) {
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
