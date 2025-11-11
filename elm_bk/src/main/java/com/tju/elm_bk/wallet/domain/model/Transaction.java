package com.tju.elm_bk.wallet.domain.model;

import com.tju.elm_bk.wallet.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Long fromWalletId;
    private final Long toWalletId;
    private final String remark;
    private final LocalDateTime createTime;

    public Transaction(TransactionType type, BigDecimal amount,
                       Long fromWalletId, Long toWalletId, String remark) {
        if (type == null) {
            throw new IllegalArgumentException("交易类型不能为空");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("交易金额必须大于0");
        }

        this.type = type;
        this.amount = amount.setScale(2, BigDecimal.ROUND_HALF_UP);
        this.fromWalletId = fromWalletId;
        this.toWalletId = toWalletId;
        this.remark = remark;
        this.createTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Long getFromWalletId() { return fromWalletId; }
    public Long getToWalletId() { return toWalletId; }
    public String getRemark() { return remark; }
    public LocalDateTime getCreateTime() { return createTime; }
}
