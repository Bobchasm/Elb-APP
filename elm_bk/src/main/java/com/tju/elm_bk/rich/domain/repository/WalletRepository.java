package com.tju.elm_bk.rich.domain.repository;

import com.tju.elm_bk.rich.domain.model.Wallet;

public interface WalletRepository {

     Long createWallet(Wallet wallet);

     void modifyWallet(Wallet wallet);

     Wallet findByUserId(Long userId);

}
