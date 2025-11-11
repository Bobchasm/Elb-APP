package com.tju.elm_bk.rich.domain.model;

import com.tju.elm_bk.rich.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Transaction {
    private Long id;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Long fromWalletId;
    private final Long toWalletId;
    private final LocalDateTime createTime;
    private final BigDecimal fee;
    private final Integer status;

    public Transaction(TransactionType type, BigDecimal amount,
                       Long fromWalletId, Long toWalletId, BigDecimal fee, Integer status) {

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
        this.createTime = LocalDateTime.now();
        this.fee = fee;
        this.status = status;
    }

    public Long getId() { return id; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Long getFromWalletId() { return fromWalletId; }
    public Long getToWalletId() { return toWalletId; }
    public LocalDateTime getCreateTime() { return createTime; }
    public BigDecimal getFee() {
        return fee;
    }


}
