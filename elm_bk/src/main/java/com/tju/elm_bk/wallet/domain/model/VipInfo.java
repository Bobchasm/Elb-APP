package com.tju.elm_bk.wallet.domain.model;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.wallet.domain.model.enums.VipLevel;

import java.math.BigDecimal;

public class VipInfo {
    private final VipLevel level;

    public VipInfo(VipLevel level) {
        if (level == null) {
            throw new APIException("VIP等级不能为null");
        }
        this.level = level;
    }

    public BigDecimal applyDiscount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new APIException("金额必须大于0");
        }
        return amount.multiply(level.getDiscountRate()).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    public boolean canUpgradeTo(VipLevel targetLevel) {
        return targetLevel.getCode() > this.level.getCode();
    }

    public VipLevel getLevel() {
        return level;
    }
}
