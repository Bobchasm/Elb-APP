package com.tju.elm.payment.domain.repository;


import com.tju.elm.payment.domain.model.Loan;
import com.tju.elm.payment.entity.VirtualWalletLoan;

import java.math.BigDecimal;
import java.util.List;

public interface LoanRepository {

    void load(Long walletId, BigDecimal amount,Float rate);

    void repay(Long id);

    List<VirtualWalletLoan> getWalletLoanList(Long wallet);

    Loan getWalletLoan(Long id);
}
