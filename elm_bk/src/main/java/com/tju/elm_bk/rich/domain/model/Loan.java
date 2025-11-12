package com.tju.elm_bk.rich.domain.model;

import java.math.BigDecimal;

public class Loan {
    private Long id;
    private Long walletId;
    private BigDecimal loanAmount;


    public Loan(Long walletId, BigDecimal loanAmount) {
        this.walletId = walletId;
        this.loanAmount = loanAmount;
    }

    public Long getId() {
        return id;
    }

    public Long getWalletId() {
        return walletId;
    }

    public BigDecimal getLoanAmount() {
        return loanAmount;
    }
}
