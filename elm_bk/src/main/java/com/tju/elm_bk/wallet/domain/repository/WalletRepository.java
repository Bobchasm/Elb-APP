package com.tju.elm_bk.wallet.domain.repository;

import com.tju.elm_bk.wallet.domain.model.Transaction;
import com.tju.elm_bk.wallet.domain.model.Wallet;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;

import java.time.LocalDate;
import java.util.List;

public interface WalletRepository {

     WalletVO getUserWallet();

     Long createWallet(Wallet wallet);

     void modifyWallet(Wallet wallet);

     List<WalletVipVO> getWalletVipRules();



     List<TransactionRecordVO> getTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate);

     TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId);

     TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId);

     Long createTransaction(Transaction transaction);

     void thawTransaction(Long transactionId, Integer status);
}
