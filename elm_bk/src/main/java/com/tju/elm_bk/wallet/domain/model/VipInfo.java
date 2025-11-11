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


    public boolean canUpgradeTo(VipLevel targetLevel) {
        return targetLevel.getCode() > this.level.getCode();
    }

    public VipLevel getLevel() {
        return level;
    }
}
