package com.tju.elm.payment.domain.infrastructure.persistence.repository;

import com.tju.elm.payment.domain.infrastructure.assembler.WalletAssembler;
import com.tju.elm.payment.domain.model.Wallet;
import com.tju.elm.payment.domain.repository.WalletRepository;
import com.tju.elm.payment.entity.VirtualWallet;
import com.tju.elm.payment.mapper.VirtualWalletMapper;
import com.tju.elm.payment.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class WalletRepositoryImpl implements WalletRepository {
    @Autowired
    private VirtualWalletMapper virtualWalletMapper;
    @Autowired
    private VirtualWalletTransactionMapper virtualWalletTransactionMapper;
//    @Autowired
//    private UserMapper userMapper;
    @Autowired
    private WalletAssembler walletAssembler;


    @Override
    public Long createWallet(Wallet wallet) {
        VirtualWallet virtualWallet = walletAssembler.toPO(wallet);
        return virtualWalletMapper.createVirtualWallet(virtualWallet);
    }

    @Override
    public void modifyWallet(Wallet wallet) {
        virtualWalletMapper.updateWallet(walletAssembler.toPO(wallet));
    }

    @Override
    public Wallet findByUserId(Long userId) {
        VirtualWallet virtualWallet = virtualWalletMapper.getWalletById(userId);
        return walletAssembler.toDomain(virtualWallet);
    }


}
