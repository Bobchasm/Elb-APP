package com.tju.elm_bk.rich.domain.application.service;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.Business;
import com.tju.elm_bk.pojo.entity.Order;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.rich.domain.model.Loan;
import com.tju.elm_bk.rich.domain.model.VipInfo;
import com.tju.elm_bk.rich.domain.repository.LoanRepository;
import com.tju.elm_bk.rich.domain.repository.VipInfoRepository;
import com.tju.elm_bk.rich.domain.web.vo.PreviewVO;
import com.tju.elm_bk.rich.entity.VirtualWalletLoan;
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
    @Autowired
    private VipInfoRepository vipInfoRepository;
    @Autowired
    private LoanRepository loanRepository;


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
        Business business = businessMapper.selectBusinessById(order.getBusinessId());
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

        // 顾客出账
        BigDecimal loadAmount = fromWallet.pay(order.getOrderTotal());
        walletRepository.modifyWallet(fromWallet);

        // 是否需要贷款，办理退款
        if (!loadAmount.equals(BigDecimal.ZERO)) {
            loanRepository.load(fromWallet.getId(),loadAmount);
        }

        // 交易，此时商家账户还未进账，金额暂留在交易中
        Transaction transaction = new Transaction(TransactionType.PAYMENT,order.getOrderTotal(),fromWallet.getId(),toWallet.getId(), BigDecimal.ZERO,1);
        transactionRepository.payOrder(transaction,orderId);
        // 设置订单已支付状态
        ordersMapper.setOrderState(orderId,1);
        // 设置支付方式
        ordersMapper.setOrderPaymentMethod(orderId,2);

        return true;
    }


    public Boolean recharge(BigDecimal amount) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (null == wallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(RECHARGE_RATE));

        wallet.collection(amount.add(fee));
        walletRepository.modifyWallet(wallet);

        Transaction transaction = new Transaction(TransactionType.RECHARGE,amount.add(fee),0L,wallet.getId(),fee,0);
        transactionRepository.createTransaction(transaction,RECHARGE_RATE);

        return true;
    }


    public Boolean withdrawal(BigDecimal amount) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (null == wallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }

        BigDecimal fee = amount.multiply(BigDecimal.valueOf(WITHDRAWAL_RATE));

        if (!wallet.getBalance().canAfford(amount.add(fee))) {
            throw new APIException(ResultCodeEnum.BALANCE_LIMIT.getMessage());
        }

        wallet.pay(amount.add(fee));
        walletRepository.modifyWallet(wallet);
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL,amount.add(fee),wallet.getId(),0L,fee,0);
        transactionRepository.createTransaction(transaction,WITHDRAWAL_RATE);

        return true;
    }

    public PreviewVO getPreview(BigDecimal amount, Integer option) {
        BigDecimal fee = amount.multiply(BigDecimal.valueOf(option == 0 ? RECHARGE_RATE : WITHDRAWAL_RATE));

        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        BigDecimal total = amount.add(fee);

        return new PreviewVO(amount, fee, total, (option == 0 ? RECHARGE_RATE : WITHDRAWAL_RATE), option == 0 || wallet.getBalance().canAfford(total));
    }

    public Long open() {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if (null != wallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_OPENED);
        }
        wallet = new Wallet(user.getId());
        return walletRepository.createWallet(wallet);
    }

    public Boolean applyVip(Integer toVipLevel) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        VipInfo vip = vipInfoRepository.findByLevel(toVipLevel);

        if (!wallet.compareVipLevel(vip)) {
            throw new APIException("您已经拥有该项vip的权益");
        }

        setVipInfo(wallet,vip);

        wallet.upgrade(vip.getOverdraftLimit());
        walletRepository.modifyWallet(wallet);

        return true;
    }

    private void setVipInfo(Wallet wallet,VipInfo vipInfo) {
        try {
            java.lang.reflect.Field vipInfoField = Wallet.class.getDeclaredField("vipInfo");
            vipInfoField.setAccessible(true);
            vipInfoField.set(wallet, vipInfo);
        } catch (Exception e) {
            throw new RuntimeException("设置VIP信息失败", e);
        }
    }

    public List<VirtualWalletLoan> getWalletLoanList() {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        return loanRepository.getWalletLoanList(wallet.getId());
    }

    public Boolean repayLoan(Long id) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        Loan loan = loanRepository.getWalletLoan(id);
        if (!Objects.equals(loan.getWalletId(), wallet.getId())) {
            throw new APIException("操作失败");
        }

        loanRepository.repay(id);
        wallet.repay(loan.getLoanAmount());
        walletRepository.modifyWallet(wallet);

        return true;
    }

}
