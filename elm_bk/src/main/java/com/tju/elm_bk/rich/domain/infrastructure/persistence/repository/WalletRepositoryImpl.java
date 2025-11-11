package com.tju.elm_bk.rich.domain.infrastructure.persistence.repository;

import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.rich.domain.infrastructure.assembler.WalletAssembler;
import com.tju.elm_bk.rich.domain.model.Wallet;
import com.tju.elm_bk.rich.domain.repository.WalletRepository;
import com.tju.elm_bk.rich.entity.VirtualWallet;
import com.tju.elm_bk.rich.mapper.VirtualWalletMapper;
import com.tju.elm_bk.rich.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
