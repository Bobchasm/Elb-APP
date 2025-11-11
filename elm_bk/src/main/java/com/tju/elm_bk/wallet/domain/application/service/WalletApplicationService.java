package com.tju.elm_bk.wallet.domain.application.service;

import com.tju.elm_bk.wallet.domain.repository.TransactionRepository;
import com.tju.elm_bk.wallet.domain.repository.WalletRepository;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import com.tju.elm_bk.wallet.mapper.VirtualWalletMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class WalletApplicationService {
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    List<TransactionRecordVO> transactionRecord(Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        return null;
    }




}
