package com.tju.elm_bk.rich.domain.application.service;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.OrdersMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.Business;
import com.tju.elm_bk.pojo.entity.Order;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.rich.domain.infrastructure.assembler.LoanAssembler;
import com.tju.elm_bk.rich.domain.model.Loan;
import com.tju.elm_bk.rich.domain.model.VipInfo;
import com.tju.elm_bk.rich.domain.repository.LoanRepository;
import com.tju.elm_bk.rich.domain.repository.VipInfoRepository;
import com.tju.elm_bk.rich.domain.web.vo.LoanVO;
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
import com.tju.elm_bk.service.PointsService;
import com.tju.elm_bk.service.MarketingPointsRuleService;
import com.tju.elm_bk.service.OrderMessageService;
import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.mapper.OrderDetailetMapper;
import com.tju.elm_bk.pojo.vo.OrderFoodVO;
import com.tju.elm_bk.mapper.PointsExchangeOrderMapper;
import com.tju.elm_bk.pojo.entity.PointsExchangeOrder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
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
    @Autowired
    private PointsService pointsService;
    @Autowired
    private MarketingPointsRuleService marketingPointsRuleService;
    @Autowired
    private OrderMessageService orderMessageService;
    @Autowired
    private OrderDetailetMapper orderDetailetMapper;
    @Autowired
    private PointsExchangeOrderMapper pointsExchangeOrderMapper;


    public final static float RECHARGE_RATE = 0.01f;
    public final static float WITHDRAWAL_RATE = 0.05f;
    public final static float LOAD_RATE = 0.02f;

    @Autowired
    private LoanAssembler loanAssembler;


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

    /**
     * 支付订单
     * 使用悲观锁保证并发安全
     * @param orderId 订单ID
     * @return 是否成功
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean payOrder(Long orderId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        
        // 悲观锁查询订单，防止并发支付
        Order order = ordersMapper.getOrderByIdForUpdate(orderId);
        if (null == order) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }
        
        // 校验订单状态，防止重复支付
        if (order.getOrderState() != null && order.getOrderState() != 0) {
            throw new APIException(ResultCodeEnum.ORDER_ALREADY_PAID);
        }
        
        // 查询商家信息
        Business business = businessMapper.selectBusinessById(order.getBusinessId());
        if (null == business) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }
        
        // 查询商家钱包
        Wallet toWallet = walletRepository.findByUserId(business.getUserId());
        if (null == toWallet) {
            throw new APIException(ResultCodeEnum.TOUSER_VIRTUAL_WALLET_MISSED);
        }
        
        // 查询用户钱包
        Wallet fromWallet = walletRepository.findByUserId(user.getId());
        if (null == fromWallet) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }

        // 顾客出账
        BigDecimal loadAmount = fromWallet.pay(order.getOrderTotal());
        walletRepository.modifyWallet(fromWallet);

        // 是否需要贷款，办理贷款
        if (!loadAmount.equals(BigDecimal.ZERO)) {
            Loan loan = new Loan(fromWallet.getId(), loadAmount, LOAD_RATE, LocalDateTime.now());
            loanRepository.load(loan);
        }

        // 创建交易记录，此时商家账户还未进账，金额暂留在交易中
        Transaction transaction = new Transaction(TransactionType.PAYMENT,order.getOrderTotal(),fromWallet.getId(),toWallet.getId(), BigDecimal.ZERO,1);
        transactionRepository.payOrder(transaction,orderId);
        // 设置订单已支付状态
        ordersMapper.setOrderState(orderId,1);
        // 设置支付方式
        ordersMapper.setOrderPaymentMethod(orderId,2);
        
        // 订单支付完成，发送消息到RabbitMQ（异步通知营销系统计算积分）
        // 注意：积分兑换订单不需要发送消息，因为积分已扣除，不需要再发放奖励积分
        if (!isPointsExchangeOrder(orderId)) {
            sendOrderPaidMessage(orderId, order);
        }

        return true;
    }
    
    /**
     * 检查订单是否是积分兑换订单
     * @param orderId 订单ID
     * @return 是否是积分兑换订单
     */
    private boolean isPointsExchangeOrder(Long orderId) {
        // 方法1：通过支付方式判断（payment_method = 3 表示积分兑换）
        Order order = ordersMapper.getOrderById(orderId);
        if (order != null && order.getPaymentMethod() != null && order.getPaymentMethod() == 3) {
            return true;
        }
        
        // 方法2：通过积分兑换订单表判断
        PointsExchangeOrder exchangeOrder = pointsExchangeOrderMapper.selectByOrderId(orderId);
        return exchangeOrder != null;
    }
    
    /**
     * 发送订单支付完成消息到RabbitMQ
     * 设计原则：封装与抽象 - 封装消息构建和发送逻辑
     */
    private void sendOrderPaidMessage(Long orderId, Order order) {
        try {
            // 1. 查询订单详情（商品列表）
            List<OrderFoodVO> orderFoodList = orderDetailetMapper.selectOrderDetailList(orderId);
            
            // 2. 构建商品详情列表
            List<OrderPaidMessage.OrderFoodDetail> foodDetails = new ArrayList<>();
            List<Long> foodIds = new ArrayList<>();
            
            for (OrderFoodVO foodVO : orderFoodList) {
                OrderPaidMessage.OrderFoodDetail detail = new OrderPaidMessage.OrderFoodDetail();
                detail.setFoodId(foodVO.getFoodId());
                detail.setFoodPrice(foodVO.getFoodPrice());
                detail.setQuantity(foodVO.getQuantity());
                foodDetails.add(detail);
                foodIds.add(foodVO.getFoodId());
            }
            
            // 3. 构建订单支付完成消息
            OrderPaidMessage message = new OrderPaidMessage();
            message.setOrderId(orderId);
            message.setUserId(order.getCustomerId());
            message.setOrderAmount(order.getOrderTotal());
            message.setOrderDate(order.getOrderDate());
            message.setFoodIds(foodIds);
            message.setFoodDetails(foodDetails);
            
            // 4. 发送消息到RabbitMQ（异步，不阻塞订单状态更新）
            orderMessageService.sendOrderPaidMessage(message);
            log.info("虚拟钱包支付订单{}，已发送订单支付完成消息到RabbitMQ", orderId);
        } catch (Exception e) {
            // 消息发送失败不影响订单状态更新
            // 可以考虑记录日志或发送到死信队列
            log.error("发送订单支付完成消息失败: orderId={}, error={}", orderId, e.getMessage(), e);
        }
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

//        if (!wallet.getBalance().canAfford(amount.add(fee))) {
//            throw new APIException(ResultCodeEnum.BALANCE_LIMIT.getMessage());
//        }

//        wallet.pay(amount.add(fee));
        wallet.pay(amount);
        walletRepository.modifyWallet(wallet);
        Transaction transaction = new Transaction(TransactionType.WITHDRAWAL,amount.add(fee),wallet.getId(),0L,fee,0);
        transactionRepository.createTransaction(transaction,WITHDRAWAL_RATE);

        return true;
    }

    public PreviewVO getPreview(BigDecimal amount, Integer option) {
        BigDecimal fee;
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        BigDecimal total = amount;

        if (option == 0) {
            fee = amount.multiply(BigDecimal.valueOf(RECHARGE_RATE));
            total = total.add(fee);
        }
        else {
            fee = amount.multiply(BigDecimal.valueOf(WITHDRAWAL_RATE));
            total = total.subtract(fee);
        }

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
        
        // 会员升级，增加等级积分并更新积分账户的会员等级
        try {
            // 使用 vip.getLevel() 获取会员等级（这是数据库表中的 id）
            Integer memberLevel = vip.getLevel();
            if (memberLevel != null && memberLevel >= 1 && memberLevel <= 3) {
                pointsService.upgradeMemberLevel(user.getId(), memberLevel);
            }
        } catch (Exception e) {
            // 积分处理失败不影响会员升级，记录日志
            System.err.println("会员升级积分处理失败: userId=" + user.getId() + ", vipLevel=" + toVipLevel + ", error=" + e.getMessage());
        }

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

    public Boolean repayLoan(Long id,Integer option) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Wallet wallet = walletRepository.findByUserId(user.getId());
        Loan loan = loanRepository.getWalletLoan(id);
        if (!Objects.equals(loan.getWalletId(), wallet.getId())) {
            throw new APIException("操作失败");
        }

        if (option == 1) {
            if (!wallet.getBalance().canAfford(loan.getLoanAmount())) {
                throw new APIException(ResultCodeEnum.BALANCE_LIMIT);
            }
            Transaction transaction = new Transaction(TransactionType.PAYMENT,loan.getLoanAmount(),wallet.getId(),0L,loan.countInterest(),0);
            transactionRepository.createTransaction(transaction,loan.getLoanInterestRate());
        }

        loanRepository.repay(id);
        wallet.repay(loan.getLoanAmount(),option);
        walletRepository.modifyWallet(wallet);
        
        // 还贷款行为积分奖励
        try {
            marketingPointsRuleService.calculateBehaviorPoints(user.getId(), "repay_loan");
            log.info("用户{}还贷款，获得行为积分", user.getId());
        } catch (Exception e) {
            // 积分处理失败不影响还贷款，记录日志
            log.error("还贷款行为积分处理失败: userId={}, error={}", user.getId(), e.getMessage());
        }

        return true;
    }

    public LoanVO getWalletLoanById(Long loanId) {
        Loan loan = loanRepository.getWalletLoan(loanId);
        VirtualWalletLoan po = loanAssembler.toPO(loan);
        LoanVO loanVO = new LoanVO();
        BeanUtils.copyProperties(po,loanVO);
        loanVO.setInterestAmount(loan.countInterest());
        return loanVO;
    }
}
