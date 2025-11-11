package com.tju.elm_bk.rich.domain.infrastructure.assembler;

import com.tju.elm_bk.rich.domain.model.Transaction;
import com.tju.elm_bk.rich.domain.model.enums.TransactionType;
import com.tju.elm_bk.rich.entity.VirtualWalletTransaction;
import com.tju.elm_bk.rich.domain.web.vo.*;

import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class TransactionAssembler {
    // ========== Domain → PO (持久化对象) ==========

    /**
     * Transaction领域对象 → VirtualWalletTransaction持久化对象
     */
    public VirtualWalletTransaction toPO(Transaction transaction) {
        if (transaction == null) return null;

        VirtualWalletTransaction po = new VirtualWalletTransaction();
        po.setType(transaction.getType().getType());
        po.setAmount(transaction.getAmount());
        po.setFromAccount(transaction.getFromWalletId());
        po.setToAccount(transaction.getToWalletId());
        po.setFee(transaction.getFee());

        return po;
    }

    /**
     * VirtualWalletTransaction持久化对象 → Transaction领域对象
     */
    public Transaction toDomain(VirtualWalletTransaction po) {
        if (po == null) return null;

        Transaction transaction = new Transaction(
                TransactionType.fromCode(po.getType()),
                po.getAmount(),
                po.getFromAccount(),
                po.getToAccount(),
                po.getFee(),
                po.getStatus()
        );
        setId(transaction, po.getId());
        setField(transaction, "createTime", po.getCreateTime());
        return transaction;
    }


    /**
     * Transaction领域对象 → TransactionVO
     */
    public TransactionRecordVO toVO(Transaction transaction) {
        if (transaction == null) return null;

        TransactionRecordVO vo = new TransactionRecordVO();
        vo.setId(transaction.getId());
        vo.setType(transaction.getType().getType());
        vo.setAmount(transaction.getAmount());
        vo.setFee(transaction.getFee());
        vo.setCreateTime(transaction.getCreateTime());
        return vo;
    }

    /**
     * 批量转换交易VO
     */
    public List<TransactionRecordVO> toVOListFromTransactions(List<Transaction> transactions) {
        if (transactions == null) return Collections.emptyList();

        return transactions.stream()
                .map(this::toVO)
                .collect(Collectors.toList());
    }

    /**
     * 设置领域对象ID（反射方式）
     */
    private void setId(Transaction transaction, Long id) {
        try {
            java.lang.reflect.Field idField = Transaction.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(transaction, id);
        } catch (Exception e) {
            throw new RuntimeException("设置Transaction ID失败", e);
        }
    }

    /**
     * 设置领域对象属性（反射方式）
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField("fieldName");
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("设置字段失败: " + fieldName, e);
        }
    }
}
