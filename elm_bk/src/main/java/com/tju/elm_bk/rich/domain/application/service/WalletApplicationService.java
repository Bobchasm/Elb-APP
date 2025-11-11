package com.tju.elm_bk.rich.domain.application.service;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.Business;
import com.tju.elm_bk.pojo.entity.Order;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.rich.domain.model.Transaction;
import com.tju.elm_bk.rich.domain.model.Wallet;
import com.tju.elm_bk.rich.domain.model.enums.TransactionType;
import com.tju.elm_bk.rich.domain.repository.TransactionRepository;
import com.tju.elm_bk.rich.domain.repository.WalletRepository;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.rich.domain.web.vo.TransactionRecordVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
public class WalletApplicationService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private OrdersMapper ordersMapper;

    public final static float RECHARGE_RATE = 0.01f;
    public final static float WITHDRAWAL_RATE = 0.05f;

    public List<TransactionRecordVO> transactionRecord(Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = getWalletByUserId(user.getId());
        return transactionRepository.getTransactionRecord(wallet.getId(),type,status,startDate,endDate);
    }

    public TransactionRecordDetailVO transactionRecordDetail(Long transactionId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = getWalletByUserId(user.getId());
        if (!wallet.verifyUser(user.getId())) {
            throw new APIException(ResultCodeEnum.USER_UNMATCHED);
        }
        return transactionRepository.getTransactionRecordDetail(transactionId);
    }

    public TransactionRecordDetailVO transactionRecordDetailByOrder(Long orderId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Order order = ordersMapper.getOrderById(orderId);
        Business business = businessMapper.selectById(order.getBusinessId());
        if (!Objects.equals(order.getCustomerId(), user.getId()) && (null != business && !Objects.equals(business.getUserId(), user.getId()))) {
            throw new APIException(ResultCodeEnum.USER_UNMATCHED);
        }
        return transactionRepository.getTransactionRecordDetailByOrder(orderId);
    }


    private Wallet getWalletByUserId(Long userId) {
        Wallet wallet = walletRepository.findByUserId(userId);
        if (wallet == null) {
            throw new IllegalArgumentException("用户未开通钱包");
        }
        return wallet;
    }

    public Boolean payOrder(Long orderId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Order order = ordersMapper.getOrderById(orderId);
        if (null == order) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }
        Business business = businessMapper.selectBusinessById(order.getId());
        if (null == business) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }
        Wallet toWallet = walletRepository.findByUserId(business.getUserId());
        if (null == toWallet) {
            throw new APIException(ResultCodeEnum.TOUSER_VIRTUAL_WALLET_MISSED);
        }
        Wallet fromWallet = walletRepository.findByUserId(user.getId());
        if (null == fromWallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }

        fromWallet.pay(order.getOrderTotal());
        walletRepository.modifyWallet(fromWallet);
        Transaction transaction = new Transaction(TransactionType.PAYMENT,order.getOrderTotal(),fromWallet.getId(),toWallet.getId(), BigDecimal.ZERO,1);
        transactionRepository.payOrder(transaction,orderId);
        ordersMapper.setOrderState(orderId,1);

        return true;
    }


    public Boolean recharge(BigDecimal amount) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (null == wallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }
        wallet.collection(amount);
        walletRepository.modifyWallet(wallet);

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(RECHARGE_RATE));

        Transaction transaction = new Transaction(TransactionType.RECHARGE,amount.add(fee),0L,wallet.getId(),fee,0);
        transactionRepository.createTransaction(transaction);

        return true;
    }


    public Boolean withdrawal(BigDecimal amount) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (null == wallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(WITHDRAWAL_RATE));

        if (wallet.getBalance().canAfford(amount.add(fee))) {
            throw new APIException(ResultCodeEnum.BALANCE_LIMIT.getMessage() + "余额：" + wallet.getBalance());
        }

        wallet.pay(amount.add(fee));
        walletRepository.modifyWallet(wallet);
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL,amount.add(fee),wallet.getId(),0L,fee,0);
        transactionRepository.createTransaction(transaction);

        return true;
    }

}
