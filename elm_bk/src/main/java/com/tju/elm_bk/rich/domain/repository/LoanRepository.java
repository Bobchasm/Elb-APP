package com.tju.elm_bk.rich.domain.repository;

import com.tju.elm_bk.rich.domain.model.Loan;
import com.tju.elm_bk.rich.entity.VirtualWalletLoan;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository {

    void load(Long walletId, BigDecimal amount,Float rate);

    void repay(Long id);

    List<VirtualWalletLoan> getWalletLoanList(Long wallet);

    Loan getWalletLoan(Long id);
}
