package com.tju.elm_bk.wallet.domain.model;


import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.wallet.domain.model.enums.VipLevel;
import com.tju.elm_bk.wallet.domain.model.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {
    private Long id;
    private final Long userId;

    private Balance balance;
    private OverdraftLimit overdraftLimit;
    private BigDecimal overdrawnAmount; // 已透支金额

    private WalletStatus status;
    private VipInfo vipInfo;

    private final LocalDateTime createTime;
    private LocalDateTime updateTime;

    public Wallet(Long userId) {
        if (userId == null) {
            throw new APIException("用户ID不能为空");
        }
        this.userId = userId;
        this.balance = Balance.ZERO;
        this.overdraftLimit = OverdraftLimit.ZERO;
        this.overdrawnAmount = BigDecimal.ZERO;
        this.status = WalletStatus.ACTIVE;
        this.vipInfo = new VipInfo(VipLevel.NORMAL);
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
    }


    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Balance getBalance() { return balance; }
    public OverdraftLimit getOverdraftLimit() { return overdraftLimit; }
    public BigDecimal getOverdrawnAmount() { return overdrawnAmount; }
    public WalletStatus getStatus() { return status; }
    public VipInfo getVipInfo() { return vipInfo; }
    public LocalDateTime getCreateTime() { return createTime; }
    public LocalDateTime getUpdateTime() { return updateTime; }
}
