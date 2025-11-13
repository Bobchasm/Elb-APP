package com.tju.elm_bk.rich.domain.model;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Loan {
    private Long id;
    private Long walletId;
    private BigDecimal loanAmount;
    private Float loanInterestRate;
    private LocalDateTime loanDate;

    public LocalDateTime getLoanDate() {
        return loanDate;
    }

    public LocalDateTime getRepayTime() {
        return repayTime;
    }

    private LocalDateTime repayTime;

    public Loan(Long walletId, BigDecimal loanAmount, Float loanInterestRate, LocalDateTime loanDate) {
        this.walletId = walletId;
        this.loanAmount = loanAmount;
        this.loanInterestRate = loanInterestRate;
        this.loanDate = loanDate;
    }

    public Loan(Long walletId, BigDecimal loanAmount, Float loanInterestRate, LocalDateTime loanDate, LocalDateTime repayTime) {
        this.walletId = walletId;
        this.loanAmount = loanAmount;
        this.loanInterestRate = loanInterestRate;
        this.loanDate = loanDate;
        this.repayTime = repayTime;
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

    public Float getLoanInterestRate() {
        return loanInterestRate;
    }

    public BigDecimal countInterest() {
        long month;
        if(null != repayTime) {
            month = ChronoUnit.MONTHS.between(loanDate.toLocalDate(), repayTime.toLocalDate());
        }else{
            LocalDate now = LocalDate.now();
            month = ChronoUnit.MONTHS.between(loanDate.toLocalDate(),now);
        }

        return loanAmount.multiply(BigDecimal.valueOf(month * loanInterestRate));
    }
}
