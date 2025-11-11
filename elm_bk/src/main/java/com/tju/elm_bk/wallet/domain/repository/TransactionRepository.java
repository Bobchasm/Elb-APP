package com.tju.elm_bk.wallet.domain.repository;

import com.tju.elm_bk.pojo.entity.Order;
import com.tju.elm_bk.wallet.domain.model.Transaction;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepository {
    List<TransactionRecordVO> getTransactionRecord(Long walletId,Integer type, Integer status, LocalDate startDate, LocalDate endDate);

    TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId);

    TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId);

    Transaction getTransactionByOrder(Long orderId);

    void payOrder(Transaction transaction,Long orderId);

    void thawTransaction(Long transactionId, Integer status);

    void createTransaction(Transaction transaction);



}
