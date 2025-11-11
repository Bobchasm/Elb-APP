package com.tju.elm_bk.wallet.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.SystemConfigMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.wallet.domain.infrastructure.assembler.WalletAssembler;
import com.tju.elm_bk.wallet.domain.model.Transaction;
import com.tju.elm_bk.wallet.domain.model.Wallet;
import com.tju.elm_bk.wallet.domain.repository.WalletRepository;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import com.tju.elm_bk.wallet.entity.VirtualWallet;
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
    @Autowired
    private WalletAssembler walletAssembler;


    @Override
    public Long createWallet(Wallet wallet) {
        return null;
    }

    @Override
    public void modifyWallet(Wallet wallet) {
        virtualWalletMapper.updateWallet(wallet.getId(),walletAssembler.toPO(wallet));
    }

    @Override
    public Wallet findByUserId(Long userId) {
        VirtualWallet virtualWallet = virtualWalletMapper.getWalletById(userId);
        return walletAssembler.toDomain(virtualWallet);
    }


}
