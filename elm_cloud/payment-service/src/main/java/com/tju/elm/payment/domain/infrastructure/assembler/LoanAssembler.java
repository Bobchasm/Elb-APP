package com.tju.elm.payment.domain.infrastructure.assembler;

import com.tju.elm.payment.domain.model.Loan;
import com.tju.elm.payment.entity.VirtualWalletLoan;
import org.springframework.stereotype.Component;

@Component
public class LoanAssembler {
    public VirtualWalletLoan toPO(Loan loan) {
        if (loan == null) return null;

        VirtualWalletLoan po = new VirtualWalletLoan();
        po.setId(loan.getId());
        po.setWalletId(loan.getWalletId());
        po.setLoanAmount(loan.getLoanAmount());
        po.setRate(loan.getLoanInterestRate());
        po.setCreateTime(loan.getLoanDate());
        po.setRepayTime(loan.getRepayTime());

        return po;
    }

    public Loan toDomain(VirtualWalletLoan po) {
        if (po == null) return null;

        Loan loan = new Loan(
                po.getWalletId(),
                po.getLoanAmount(),
                po.getRate(),
                po.getCreateTime(),
                po.getRepayTime()
        );
        setId(loan, po.getId());
        return loan;
    }

    private void setId(Loan loan, Long id) {
        try {
            java.lang.reflect.Field idField = Loan.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(loan, id);
        } catch (Exception e) {
            throw new RuntimeException("设置 ID失败", e);
        }
    }
}
