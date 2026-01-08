package com.tju.elm.payment.domain.infrastructure.persistence.repository;

import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.po.User;
import com.tju.elm.payment.domain.infrastructure.assembler.TransactionAssembler;
import com.tju.elm.payment.domain.model.Transaction;
import com.tju.elm.payment.domain.repository.TransactionRepository;
import com.tju.elm.payment.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm.payment.domain.web.vo.TransactionRecordVO;
import com.tju.elm.payment.entity.VirtualWalletTransaction;
import com.tju.elm.payment.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class TransactionRepositoryImpl implements TransactionRepository {
    @Autowired
    private VirtualWalletTransactionMapper virtualWalletTransactionMapper;
    @Autowired
    private TransactionAssembler transactionAssembler;

    @Autowired
    private UserClient userClient;

    @Override
    public List<TransactionRecordVO> getTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        return virtualWalletTransactionMapper.queryTransactionRecord(walletId,type,status,startDate,endDate);
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetail(Long transactionId) {
        TransactionRecordDetailVO ret = virtualWalletTransactionMapper.queryTransactionRecordDetail(transactionId);

        User fromUser = userClient.gainUserById(ret.getFromAccount()).getData();
        ret.setFromAccountName(fromUser != null ? fromUser.getUsername() : null);

        User toUser = userClient.gainUserById(ret.getToAccount()).getData();
        ret.setToAccountName(toUser != null ? toUser.getUsername() : null);

        return ret;
    }

    @Override
    public TransactionRecordDetailVO getTransactionRecordDetailByOrder(Long orderId) {
        TransactionRecordDetailVO ret = virtualWalletTransactionMapper.queryTransactionByOrder(orderId);

        if (ret != null) {
            User fromUser = userClient.gainUserById(ret.getFromAccount()).getData();
            ret.setFromAccountName(fromUser != null ? fromUser.getUsername() : null);

            User toUser = userClient.gainUserById(ret.getToAccount()).getData();
            ret.setToAccountName(toUser != null ? toUser.getUsername() : null);
        }

        return ret;
    }

    @Override
    public Transaction getTransactionByOrder(Long orderId) {
        VirtualWalletTransaction virtualWalletTransaction = virtualWalletTransactionMapper.getTransactionByOrder(orderId);
        return transactionAssembler.toDomain(virtualWalletTransaction);
    }

    @Override
    public void payOrder(Transaction transaction, Long orderId) {
        VirtualWalletTransaction virtualWalletTransaction = transactionAssembler.toPO(transaction,null);
        virtualWalletTransaction.setOrderId(orderId);
        virtualWalletTransactionMapper.createTransaction(virtualWalletTransaction);
    }

    @Override
    public void thawTransaction(Long transactionId, Integer status) {
        virtualWalletTransactionMapper.thawTransaction(transactionId,status);
    }

    @Override
    public void createTransaction(Transaction transaction,Float feeRate) {
        VirtualWalletTransaction virtualWalletTransaction = transactionAssembler.toPO(transaction,feeRate);
        virtualWalletTransactionMapper.createTransaction(virtualWalletTransaction);
    }


}
