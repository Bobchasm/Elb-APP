package com.tju.elm_bk.rich.domain.model;


import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.rich.domain.model.enums.WalletStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Wallet {
    private Long id;
    private final Long userId;

    private Balance balance;
    private OverdraftLimit overdraftLimit; // 透支限制
    private BigDecimal overdrawnAmount; // 已透支金额

    private WalletStatus status;
    private VipInfo vipInfo;

    private LocalDateTime createTime;
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
        this.vipInfo = new VipInfo(0,BigDecimal.ZERO,BigDecimal.ZERO);
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


    public Boolean verifyUser(Long userId) {
        return this.userId.equals(userId);
    }

    public BigDecimal pay(BigDecimal amount) {
        validateActiveStatus();
        BigDecimal totalAvailable = balance.getAmount()
                .add(overdraftLimit.getAmount())
                .subtract(overdrawnAmount);

        // 余额+可贷款不足
        if (totalAvailable.compareTo(amount) < 0) {
            throw new APIException(ResultCodeEnum.BALANCE_LIMIT);
        }

        // 优先使用余额
        BigDecimal balancePayment = balance.getAmount().min(amount);
        BigDecimal overdraftPayment = amount.subtract(balancePayment);
        if (balancePayment.compareTo(BigDecimal.ZERO) > 0) {
            balance = balance.subtract(balancePayment);
        }
        if (overdraftPayment.compareTo(BigDecimal.ZERO) > 0) {
            overdrawnAmount = overdrawnAmount.add(overdraftPayment);
        }

        return overdraftPayment;
    }

    public void collection(BigDecimal amount) {
        validateActiveStatus();
        balance = balance.add(amount);
    }

    public void repay(BigDecimal amount,Integer option) {
        overdrawnAmount = overdrawnAmount.subtract(amount);
        if (option == 1) {
            balance = balance.subtract(amount);
        }
    }


    private void validateActiveStatus() {
        if (this.status != WalletStatus.ACTIVE) {
            throw new APIException("钱包状态异常，无法操作");
        }
    }

    public boolean compareVipLevel(VipInfo vipLevel) {
        return this.vipInfo.canUpgradeTo(vipLevel);
    }

    public void upgrade(BigDecimal overdraftLimitAmount) {
        overdraftLimit.upgrade(overdraftLimitAmount);
    }
}
