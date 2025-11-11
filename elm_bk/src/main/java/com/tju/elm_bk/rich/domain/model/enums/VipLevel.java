package com.tju.elm_bk.rich.domain.model.enums;

import java.math.BigDecimal;

public enum VipLevel {
    NORMAL(0, "普通用户", BigDecimal.ZERO),
    SILVER(1, "白银会员", new BigDecimal("0.95")),
    GOLD(2, "黄金会员", new BigDecimal("0.90")),
    PLATINUM(3, "白金会员", new BigDecimal("0.85"));

    private final int code;
    private final String description;
    private final BigDecimal discountRate; // 折扣率

    VipLevel(int code, String description, BigDecimal discountRate) {
        this.code = code;
        this.description = description;
        this.discountRate = discountRate;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }
    public BigDecimal getDiscountRate() { return discountRate; }

    public static VipLevel fromCode(int code) {
        for (VipLevel level : values()) {
            if (level.code == code) {
                return level;
            }
        }
        throw new IllegalArgumentException("未知的VIP等级: " + code);
    }
}
