package com.tju.elm_bk.wallet.domain.repository;

import com.tju.elm_bk.wallet.domain.model.Wallet;

public interface WalletRepository {

     Long createWallet(Wallet wallet);

     void modifyWallet(Wallet wallet);

     Wallet findByUserId(Long userId);

}
