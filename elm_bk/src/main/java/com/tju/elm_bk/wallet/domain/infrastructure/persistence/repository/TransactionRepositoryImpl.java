package com.tju.elm_bk.wallet.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.wallet.domain.model.Transaction;
import com.tju.elm_bk.wallet.domain.repository.TransactionRepository;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {
    @Override
    public List<TransactionRecordVO> getTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        return List.of();
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId) {
        return null;
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId) {
        return null;
    }

    @Override
    public Long createTransaction(Transaction transaction) {
        return null;
    }

    @Override
    public void thawTransaction(Long transactionId, Integer status) {

    }
}
