package com.tju.elm_bk.rich.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.rich.domain.infrastructure.assembler.TransactionAssembler;
import com.tju.elm_bk.rich.domain.model.Transaction;
import com.tju.elm_bk.rich.domain.repository.TransactionRepository;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.rich.entity.VirtualWalletTransaction;
import com.tju.elm_bk.rich.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {
    @Autowired
    private VirtualWalletTransactionMapper virtualWalletTransactionMapper;
    @Autowired
    private TransactionAssembler transactionAssembler;

    @Override
    public List<TransactionRecordVO> getTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        return virtualWalletTransactionMapper.queryTransactionRecord(walletId,type,status,startDate,endDate);
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId) {
        return virtualWalletTransactionMapper.queryTransactionRecordDetail(transactionId);
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId) {
        return virtualWalletTransactionMapper.queryTransactionByOrder(orderId);
    }

    @Override
    public Transaction getTransactionByOrder(Long orderId) {
        VirtualWalletTransaction virtualWalletTransaction = virtualWalletTransactionMapper.getTransactionByOrder(orderId);
        return transactionAssembler.toDomain(virtualWalletTransaction);
    }

    @Override
    public void payOrder(Transaction transaction,Long orderId) {
        VirtualWalletTransaction virtualWalletTransaction = transactionAssembler.toPO(transaction);
        virtualWalletTransaction.setOrderId(orderId);
        virtualWalletTransactionMapper.createTransaction(virtualWalletTransaction);
    }

    @Override
    public void thawTransaction(Long transactionId, Integer status) {
        virtualWalletTransactionMapper.thawTransaction(transactionId,status);
    }

    @Override
    public void createTransaction(Transaction transaction) {
        VirtualWalletTransaction virtualWalletTransaction = transactionAssembler.toPO(transaction);
        virtualWalletTransactionMapper.createTransaction(virtualWalletTransaction);
    }


}
