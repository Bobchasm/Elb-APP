package com.tju.elm_bk.rich.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.rich.domain.infrastructure.assembler.LoanAssembler;
import com.tju.elm_bk.rich.domain.model.Loan;
import com.tju.elm_bk.rich.domain.repository.LoanRepository;
import com.tju.elm_bk.rich.domain.web.vo.LoanVO;
import com.tju.elm_bk.rich.entity.VirtualWallet;
import com.tju.elm_bk.rich.entity.VirtualWalletLoan;
import com.tju.elm_bk.rich.mapper.VirtualWalletLoanMapper;
import org.springframework.beans.BeanUtils;
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
    public void load(Long walletId, BigDecimal amount) {
        virtualWalletLoanMapper.load(walletId,amount);
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
