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

     Long createWallet(Wallet wallet);

     void modifyWallet(Wallet wallet);

}
