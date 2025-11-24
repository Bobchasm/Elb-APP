package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.pojo.dto.OrderDTO;
import com.tju.elm_bk.pojo.dto.OrderPaidMessage;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.*;
import com.tju.elm_bk.pojo.entity.*;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.MarketingPointsExchangeRuleService;
import com.tju.elm_bk.service.OrderMessageService;
import com.tju.elm_bk.service.OrderService;
import com.tju.elm_bk.service.PointsService;
import lombok.extern.slf4j.Slf4j;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.pojo.vo.CartItemVO;
import com.tju.elm_bk.pojo.vo.OrderFoodVO;
import com.tju.elm_bk.pojo.vo.OrderItemDetailVO;
import com.tju.elm_bk.pojo.vo.OrderItemVO;
import com.tju.elm_bk.pojo.vo.OrderVO;
import com.tju.elm_bk.rich.domain.model.Transaction;
import com.tju.elm_bk.rich.domain.model.Wallet;
import com.tju.elm_bk.rich.domain.model.enums.TransactionType;
import com.tju.elm_bk.rich.domain.repository.TransactionRepository;
import com.tju.elm_bk.rich.domain.repository.WalletRepository;
import com.tju.elm_bk.websocket.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private FoodMapper foodMapper;
    @Autowired
    private OrderDetailetMapper orderDetailetMapper;
    @Autowired
    private DeliveryAddressMapper deliveryAddressMapper;
    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private WalletRepository walletRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    @Autowired
    private OrderMessageService orderMessageService;
    @Autowired
    private PointsService pointsService;
    @Autowired
    private MarketingPointsExchangeRuleService exchangeRuleService;

    // 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消
    public static final List<Integer> orderStatusList;

    static {
        orderStatusList = List.of(0,1,2,3,4);
    }

    @Autowired
    private CartMapper cartMapper;

    @Override
    public List<OrderVO> getCustomerOrderList(Long customerId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        if (!Objects.equals(customerId, user.getId()) &&
                user.getAuthorities().stream().noneMatch(auth -> "ADMIN".equals(auth.getName()))) {
            throw new APIException(ResultCodeEnum.USER_UNMATCHED);
        }
        return ordersMapper.selectOrders(customerId);
    }

    @Override
    public OrderVO getOrderById(Long orderId) {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        OrderVO orderVO = ordersMapper.selectOrderById(orderId);
        if (orderVO == null) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }
        Business business = businessMapper.selectBusinessById(orderId);
        if(!Objects.equals(orderVO.getCustomer().getId(), user.getId()) && !Objects.equals(orderVO.getBusiness().getId(), business.getId())) {
            throw new APIException(ResultCodeEnum.USER_UNMATCHED);
        }
        return orderVO;
    }

    @Override
    @Transactional
    public OrderVO addOrder(OrderDTO orderDTO) {
        if (!orderDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Business business = businessMapper.selectBusinessById(orderDTO.getBusiness().getId());
        if (business == null) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }
        DeliveryAddress deliveryAddress = deliveryAddressMapper.getDeliveryAddressById(orderDTO.getDeliveryAddress().getId());
        if (deliveryAddress == null) {
            throw new APIException(ResultCodeEnum.ADDRESS_MISSED);
        }

        // 设置订单信息
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        if (!Objects.equals(userMapper.getUserIdByUsername(orderDTO.getCustomer().getUsername()), user.getId())) {
            throw new APIException(ResultCodeEnum.USER_UNMATCHED);
        }
        List<CartItemVO> cartItemsInBusiness = cartMapper.selectCartItems(user.getId(), business.getId());
        if (!cartItemsInBusiness.isEmpty()) {
            throw new APIException(ResultCodeEnum.CART_EMPTY);
        }
        Order order = new Order();
        order.setBusinessId(business.getId());
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerId(user.getId());
        order.setAddressId(deliveryAddress.getId());

        order.setOrderState(0);
        order.setCreator(user.getId());
        order.setUpdater(user.getId());
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDeleted(false);

        // 计算总价
        double totalPrice = 0.0;
        for (CartItemVO cartItemVO : cartItemsInBusiness) {
            totalPrice += (cartItemVO.getFoodPrice() * cartItemVO.getQuantity());
        }
        order.setOrderTotal(BigDecimal.valueOf(totalPrice));

        // 插入订单数据到数据库
        ordersMapper.insertOrder(order);

        // 插入订单详情
        for (CartItemVO cartItemVO : cartItemsInBusiness) {

            OrderDetailet orderDetailet = new OrderDetailet();

            orderDetailet.setOrderId(order.getId());
            orderDetailet.setQuantity(cartItemVO.getQuantity());
            orderDetailet.setFoodId(cartItemVO.getFoodId());

            orderDetailet.setCreator(user.getId());
            orderDetailet.setUpdater(user.getId());
            orderDetailet.setCreateTime(LocalDateTime.now());
            orderDetailet.setUpdateTime(LocalDateTime.now());
            orderDetailet.setIsDeleted(false);

            orderDetailetMapper.saveOrderDetail(orderDetailet);
        }

        // 清空该用户在当前商家的购物车
        cartMapper.clearCart(user.getId(),orderDTO.getBusiness().getId());

        return ordersMapper.selectOrderById(order.getId());
    }





    @Override
    public List<OrderItemDetailVO> getOrderItemListByBusiness(Long businessId, Integer orderState) {
        List<OrderItemDetailVO> ret = ordersMapper.selectOrderDetailetItem(businessId, orderState);
        for (OrderItemDetailVO orderItemDetailVO : ret) {
            orderItemDetailVO.setFoodList(orderDetailetMapper.selectOrderDetailList(orderItemDetailVO.getId()));
        }
        return ret;
    }

    @Override
    public List<OrderItemVO> getOrderItemListByUser(Integer orderState) {
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        return ordersMapper.selectOrderItemsList(null, orderState, userId);
    }

    @Override
    public OrderItemDetailVO getOrderItemDetail(Long orderItemId) {
        OrderItemDetailVO ret = ordersMapper.selectOrderItemById(orderItemId);
        ret.setFoodList(orderDetailetMapper.selectOrderDetailList(orderItemId));
        return ret;
    }

    @Override
    @Transactional
    public Long setOrderState(Long orderId, Integer orderState, Boolean usePoints) {
        if (!orderStatusList.contains(orderState)) {
            throw new APIException(ResultCodeEnum.ORDER_STATUS_UNMATCHED);
        }
        Order order = ordersMapper.getOrderById(orderId);
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        if (null == order) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }

        Business business = businessMapper.selectBusinessById(order.getBusinessId());

        // 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消)
        if (orderState == 1) {
            if (order.getOrderState() != 0 || !Objects.equals(userId, order.getCustomerId())) {
                throw new APIException(ResultCodeEnum.ORDER_PAY_FAILED);
            }
            
            // 积分+现金支付：根据usePoints参数决定是否使用积分抵扣
            // usePoints=true 或 null（默认）：使用积分抵扣
            // usePoints=false：不使用积分，只用现金支付
            try {
                // 判断是否使用积分（默认使用积分）
                boolean shouldUsePoints = (usePoints == null || usePoints);
                
                if (shouldUsePoints) {
                    // 1. 计算可抵扣金额
                    BigDecimal deductibleAmount = pointsService.calculateDeductibleAmount(
                        order.getCustomerId(), order.getOrderTotal());
                    
                    // 2. 如果可抵扣金额大于0，则冻结积分
                    if (deductibleAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // 获取积分兑换比例
                    BigDecimal exchangeRatio = exchangeRuleService.getCashExchangeRatio();
                    if (exchangeRatio == null || exchangeRatio.compareTo(BigDecimal.ZERO) <= 0) {
                        exchangeRatio = BigDecimal.valueOf(100); // 默认100积分=1元
                    }
                    
                    // 计算需要冻结的积分数量
                    Long pointsToFreeze = deductibleAmount.multiply(exchangeRatio).longValue();
                    
                    // 冻结积分（优先扣减即将过期的积分）
                    pointsService.freezePoints(order.getCustomerId(), pointsToFreeze, orderId);
                    
                    // 更新订单的积分使用信息
                    ordersMapper.updateOrderPoints(orderId, pointsToFreeze, deductibleAmount);
                    
                        log.info("订单{}支付：使用积分{}，抵扣金额{}元", orderId, pointsToFreeze, deductibleAmount);
                    } else {
                        // 没有可用积分，只使用现金支付
                        ordersMapper.updateOrderPoints(orderId, 0L, BigDecimal.ZERO);
                        log.info("订单{}支付：没有可用积分，只使用现金支付", orderId);
                    }
                } else {
                    // 用户选择不使用积分，只使用现金支付
                    ordersMapper.updateOrderPoints(orderId, 0L, BigDecimal.ZERO);
                    log.info("订单{}支付：用户选择不使用积分，只使用现金支付", orderId);
                }
            } catch (Exception e) {
                // 积分处理失败不影响订单支付，记录日志
                log.error("订单{}支付时积分处理失败: {}", orderId, e.getMessage());
                // 如果积分处理失败，只使用现金支付
                ordersMapper.updateOrderPoints(orderId, 0L, BigDecimal.ZERO);
            }
            
            // 订单支付完成，发送消息到RabbitMQ（异步通知营销系统）
            sendOrderPaidMessage(orderId, order);
        }
        if (orderState == 2) {
            if (order.getOrderState() != 1 || !Objects.equals(business.getUserId(),userId)) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }
        }
        if (orderState == 3) {
            if (order.getOrderState() != 2 || (!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId))) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }

            if (order.getPaymentMethod() == 2) {
                // 商家用户钱包
                Wallet wallet = walletRepository.findByUserId(business.getUserId());
                // 订单交易
                Transaction transaction = transactionRepository.getTransactionByOrder(orderId);
                // 交易金额解冻
                transactionRepository.thawTransaction(transaction.getId(), 0);
                wallet.collection(transaction.getAmount());
                walletRepository.modifyWallet(wallet);
            }
            
            // 订单完成，处理积分相关逻辑
            try {
                // 1. 真正扣除用户支付的积分（如果使用了积分+现金支付）
                if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                    pointsService.deductFrozenPoints(order.getCustomerId(), orderId);
                    log.info("订单{}完成：扣除积分{}", orderId, order.getPointsUsed());
                }
                
                // 2. 解冻奖励积分（订单支付时已发放但冻结，现在解冻）
                pointsService.unfreezeRewardPoints(order.getCustomerId(), orderId);
            } catch (Exception e) {
                // 积分处理失败不影响订单完成，记录日志
                log.error("订单{}完成时积分处理失败: {}", orderId, e.getMessage());
            }
        }
        if (orderState == 4) {
            if (order.getOrderState() != 0 && order.getOrderState() != 1) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_DENY);
            }
            if ((!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId))) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_FAILED);
            }

            if (order.getPaymentMethod() == 2) {
                Wallet my_wallet = walletRepository.findByUserId(userId);
                Wallet bu_wallet = walletRepository.findByUserId(business.getUserId());

                // 该订单支付交易永久冻结标记
                Transaction transaction = transactionRepository.getTransactionByOrder(orderId);
                transactionRepository.thawTransaction(transaction.getId(), 3);

                // 退款交易
                Transaction back_transaction = new Transaction(TransactionType.REFUND, transaction.getAmount(), bu_wallet.getId(), my_wallet.getId(), BigDecimal.ZERO, 0);
                transactionRepository.payOrder(back_transaction, orderId);

                // 钱包退款到账
                my_wallet.collection(order.getOrderTotal());
                walletRepository.modifyWallet(my_wallet);
            }
            
            // 订单取消，取消奖励积分（如果已发放）和解冻用户支付的积分（如果使用了积分+现金）
            try {
                // 取消奖励积分（如果订单支付时已发放）
                pointsService.cancelRewardPoints(order.getCustomerId(), orderId);
                // 解冻用户支付的积分（如果使用了积分+现金支付）
                if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                    pointsService.unfreezePoints(order.getCustomerId(), orderId);
                }
            } catch (Exception e) {
                // 积分处理失败不影响订单取消，记录日志
                System.err.println("处理积分失败: orderId=" + orderId + ", error=" + e.getMessage());
            }
        }
        ordersMapper.setOrderState(orderId, orderState);
        // 订单状态更新后，推送消息给相关用户
        Order order1 = ordersMapper.getOrderById(orderId);
        // 1. 推送给商家（如果订单关联了商家）
        if (order1.getBusinessId() != null) {
            Business business1 = businessMapper.selectBusinessById(order1.getBusinessId());
            if (business1 != null && business1.getUserId() != null) {
                webSocketServer.sendToClient(business1.getUserId().toString(),
                        "{\"type\": \"order_update\", \"orderId\": " + orderId + "}");
            }
        }
        // 2. 推送给顾客（订单的customerId）
        if (order1.getCustomerId() != null) {
            webSocketServer.sendToClient(order1.getCustomerId().toString(),
                    "{\"type\": \"order_update\", \"orderId\": " + orderId + "}");
        }

        return order.getId();
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
        } catch (Exception e) {
            // 消息发送失败不影响订单状态更新
            // 可以考虑记录日志或发送到死信队列
            // 这里暂时只记录日志
            System.err.println("发送订单支付完成消息失败: orderId=" + orderId + ", error=" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long orderSubmit(Long businessId,Long addressId) {
        // 商家是否存在
        Business business = businessMapper.selectBusinessById(businessId);
        if (null == business) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }

        // 设置订单信息
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        List<CartItemVO> cartItemsInBusiness = cartMapper.selectCartItems(userId,businessId);
        Order order = new Order();
        order.setBusinessId(businessId);
        order.setOrderDate(LocalDateTime.now());
        order.setCustomerId(userId);
        order.setAddressId(addressId);

        order.setOrderState(0);
        order.setCreator(userId);
        order.setUpdater(userId);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        order.setIsDeleted(false);

        // 计算总价
        double totalPrice = 0.0;
        for (CartItemVO cartItemVO : cartItemsInBusiness) {
            totalPrice += (cartItemVO.getFoodPrice() * cartItemVO.getQuantity());
        }

        if (totalPrice == 0.0) {
            throw new APIException(ResultCodeEnum.ORDER_SUBMIT_FAILED);
        }
        totalPrice += business.getDeliveryPrice().doubleValue();

        // 浮点数精度,保留两位小数
        BigDecimal price = BigDecimal.valueOf(totalPrice);
        order.setOrderTotal(price.setScale(2, RoundingMode.HALF_UP));
        // 订单保存当前商家配送费,避免商家修改导致的不一致
        order.setDeliveryPrice(business.getDeliveryPrice());

        // 插入订单数据到数据库
        ordersMapper.insertOrderPlus(order);

        // 插入订单详情
        for (CartItemVO cartItemVO : cartItemsInBusiness) {

            OrderDetailet orderDetailet = new OrderDetailet();

            orderDetailet.setOrderId(order.getId());
            orderDetailet.setQuantity(cartItemVO.getQuantity());
            orderDetailet.setFoodId(cartItemVO.getFoodId());
            // 商品价格保存到detail里,避免商家修改导致用户原有订单数据不一致
            orderDetailet.setFoodPrice(BigDecimal.valueOf(cartItemVO.getFoodPrice()));

            orderDetailet.setCreator(userId);
            orderDetailet.setUpdater(userId);
            orderDetailet.setCreateTime(LocalDateTime.now());
            orderDetailet.setUpdateTime(LocalDateTime.now());
            orderDetailet.setIsDeleted(false);

            orderDetailetMapper.saveOrderDetailPlus(orderDetailet);
        }

        // 清空该用户在当前商家的购物车
        cartMapper.clearCart(userId,businessId);

        return order.getId();
    }
}
