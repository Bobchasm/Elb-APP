package com.tju.elm.payment.domain.infrastructure.persistence.repository;

import com.tju.elm.payment.domain.infrastructure.assembler.LoanAssembler;
import com.tju.elm.payment.domain.model.Loan;
import com.tju.elm.payment.domain.repository.LoanRepository;
import com.tju.elm.payment.entity.VirtualWalletLoan;
import com.tju.elm.payment.mapper.VirtualWalletLoanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class LoanRepositoryImpl implements LoanRepository {

    @Autowired
    private VirtualWalletLoanMapper virtualWalletLoanMapper;
    @Autowired
    private LoanAssembler loanAssembler;

    @Override
    public void load(Loan loan) {
        VirtualWalletLoan po = loanAssembler.toPO(loan);
        virtualWalletLoanMapper.load(po);
    }

    @Override
    public void repay(Long id) {
        virtualWalletLoanMapper.repay(id, LocalDateTime.now());
    }

    @Override
    public List<VirtualWalletLoan> getWalletLoanList(Long wallet) {
        return virtualWalletLoanMapper.getLoanList(wallet);
    }

    @Override
    public Loan getWalletLoan(Long id) {
        VirtualWalletLoan virtualWalletLoan = virtualWalletLoanMapper.getLoan(id);
        return loanAssembler.toDomain(virtualWalletLoan);
    }
}
