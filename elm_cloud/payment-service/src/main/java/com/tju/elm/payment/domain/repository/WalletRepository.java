package com.tju.elm.payment.domain.repository;

import com.tju.elm.payment.domain.model.Wallet;

public interface WalletRepository {

     Long createWallet(Wallet wallet);

     void modifyWallet(Wallet wallet);

     Wallet findByUserId(Long userId);

}
