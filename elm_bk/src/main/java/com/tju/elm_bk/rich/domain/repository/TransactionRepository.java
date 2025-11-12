package com.tju.elm_bk.rich.domain.repository;

import com.tju.elm_bk.rich.domain.model.Transaction;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordVO;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    List<TransactionRecordVO> getTransactionRecord(Long walletId,Integer type, Integer status, LocalDate startDate, LocalDate endDate);

    TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId);

    TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId);

    Transaction getTransactionByOrder(Long orderId);

    void payOrder(Transaction transaction,Long orderId);

    void thawTransaction(Long transactionId, Integer status);

    void createTransaction(Transaction transaction,Float feeRate);



}
