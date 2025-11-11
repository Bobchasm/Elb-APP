package com.tju.elm_bk.wallet.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.wallet.domain.model.Transaction;
import com.tju.elm_bk.wallet.domain.model.Wallet;
import com.tju.elm_bk.wallet.domain.repository.WalletRepository;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import com.tju.elm_bk.wallet.mapper.VirtualWalletMapper;
import com.tju.elm_bk.wallet.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public class WalletRepositoryImpl implements WalletRepository {
    @Autowired
    private VirtualWalletMapper virtualWalletMapper;
    @Autowired
    private VirtualWalletTransactionMapper virtualWalletTransactionMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public WalletVO getUserWallet() {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        return virtualWalletMapper.queryWallet(user.getId());
    }

    @Override
    public Long createWallet(Wallet wallet) {
        return null;
    }

    @Override
    public void modifyWallet(Wallet wallet) {

    }

    @Override
    public List<WalletVipVO> getWalletVipRules() {
        return List.of();
    }

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
