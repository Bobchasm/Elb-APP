package com.tju.elm.order.service.impl;

import com.tju.elm.api.client.*;
import com.tju.elm.api.dto.TransactionDTO;
import com.tju.elm.api.po.*;
import com.tju.elm.api.vo.CartItemVO;
import com.tju.elm.order.mapper.OrderDetailetMapper;
import com.tju.elm.order.mapper.OrdersMapper;
import com.tju.elm.order.zoo.pojo.dto.CreateOrderDTO;
import com.tju.elm.order.zoo.pojo.dto.OrderPaidMessage;
import com.tju.elm.order.service.OrderMessageService;
import com.tju.elm.order.service.OrderService;
import com.tju.elm.order.zoo.pojo.vo.*;
import com.tju.elm.order.zoo.pojo.vo.Order;
import com.tju.elm.order.zoo.pojo.vo.OrderDetailet;
import com.tju.elm.order.zoo.websocket.WebSocketServer;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import result.ResultCodeEnum;
import utils.UserContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrdersMapper ordersMapper;
    @Autowired
    private OrderDetailetMapper orderDetailetMapper;

    @Autowired
    private WebSocketServer webSocketServer;

    @Autowired
    private OrderMessageService orderMessageService;

    @Autowired
    private UserClient userClient;
    @Autowired
    private BusinessClient businessClient;
    @Autowired
    private FoodClient foodClient;
    @Autowired
    private PointClient pointClient;
    @Autowired
    private PaymentClient paymentClient;

    // 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消
    public static final List<Integer> orderStatusList;

    static {
        orderStatusList = List.of(0,1,2,3,4);
    }


    @Override
    public OrderVO getOrderById(Long orderId) {
        OrderVO ret = ordersMapper.selectOrderById(orderId);
        if (ret == null) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }

        Business business = businessClient.gainBusinessById(ret.getBusinessId()).getData();
        User customerUser = userClient.gainUserById(ret.getCustomerId()).getData();
        DeliveryAddress deliveryAddress = userClient.gainDeliveryAddressById(ret.getAddressId()).getData();

        ret.setBusiness(business);
        ret.setCustomer(customerUser);
        ret.setDeliveryAddress(deliveryAddress);

        return ret;
    }

    @Override
    public List<OrderItemDetailVO> getOrderItemListByBusiness(Long businessId, Integer orderState) {
        List<OrderItemDetailVO> ret = ordersMapper.selectOrderDetailetItem(businessId, orderState);

        Set<Long> customerIdSet = ret.stream()
                .map(OrderItemDetailVO::getCustomerId)
                .collect(Collectors.toSet());

        Set<Long> addressIds = ret.stream()
                .map(OrderItemDetailVO::getAddressId)
                .collect(Collectors.toSet());

        Set<Long> businessIds = ret.stream()
                .map(OrderItemDetailVO::getBusinessId)
                .collect(Collectors.toSet());

        List<User> customers = userClient.getUserListByIds(customerIdSet).getData();
        List<DeliveryAddress> deliveryAddresses = userClient.gainAddressListByIds(addressIds).getData();
        List<Business> businesses = businessClient.gainBusinessByIds(businessIds).getData();

        for (OrderItemDetailVO item : ret) {
            User customer = item.getCustomerId() == null ? null : customers.stream()
                    .filter(u -> item.getCustomerId().equals(u.getId()))
                    .findFirst()
                    .orElse(null);

            DeliveryAddress address = item.getAddressId() == null ? null : deliveryAddresses.stream()
                    .filter(ad -> item.getAddressId().equals(ad.getId()))
                    .findFirst()
                    .orElse(null);

            Business business = item.getBusinessId() == null ? null : businesses.stream()
                    .filter(b -> item.getBusinessId().equals(b.getId()))
                    .findFirst()
                    .orElse(null);

            item.setCustomerName(customer != null ? customer.getUsername() : null);
            if (null != business) {
                item.setBusinessName(business.getBusinessName());
                item.setBusinessImg(business.getBusinessImg());
            }
            if (null != address) {
                item.setContactName(address.getContactName());
                item.setContactSex(address.getContactSex());
                item.setContactTel(address.getContactTel());
                item.setAddress(address.getAddress());
            }
        }

        for (OrderItemDetailVO orderItemDetailVO : ret) {
            List<OrderFoodVO> orderFoodList = orderDetailetMapper.selectOrderDetailList(orderItemDetailVO.getId());

            Set<Long> foodIds = orderFoodList.stream()
                    .map(OrderFoodVO::getFoodId)
                    .collect(Collectors.toSet());
            List<Food> foodList = foodClient.gainFoodsByIds(foodIds).getData();

            for (OrderFoodVO orderFoodVO : orderFoodList) {
                Food food = foodList.stream()
                        .filter(f -> orderFoodVO.getFoodId().equals(f.getId()))
                        .findFirst()
                        .orElse(null);
                orderFoodVO.setFoodName(food != null ? food.getFoodName() : null);
            }

            orderItemDetailVO.setFoodList(orderFoodList);
        }
        return ret;
    }

    @Override
    public List<OrderItemVO> getOrderItemListByUser(Integer orderState) {
        Long userId = getCurrentUserId();
        List<OrderItemVO> ret = ordersMapper.selectOrderItemsList(null, orderState, userId);

        Set<Long> businessIds = ret.stream()
                .map(OrderItemVO::getBusinessId)
                .collect(Collectors.toSet());

        List<Business> businesses = businessClient.gainBusinessByIds(businessIds).getData();

        for (OrderItemVO orderItemVO : ret) {
            Business business = orderItemVO.getBusinessId() == null ? null : businesses.stream()
                    .filter(b -> orderItemVO.getBusinessId().equals(b.getId()))
                    .findFirst()
                    .orElse(null);
            if (null != business) {
                orderItemVO.setBusinessName(business.getBusinessName());
                orderItemVO.setBusinessImg(business.getBusinessImg());
            }
        }

        return ret;
    }

    @Override
    public OrderItemDetailVO getOrderItemDetail(Long orderItemId) {
        OrderItemDetailVO ret = ordersMapper.selectOrderItemById(orderItemId);

        ret.setFoodList(orderDetailetMapper.selectOrderDetailList(orderItemId));
        Business business = businessClient.gainBusinessById(ret.getBusinessId()).getData();
        User user = userClient.gainUserById(ret.getCustomerId()).getData();
        ret.setCustomerName(user.getUsername());
        ret.setBusinessName(business.getBusinessName());
        ret.setBusinessImg(business.getBusinessImg());

        return ret;
    }

    @Override
    @Transactional
    public Long setOrderState(Long orderId, Integer orderState, Boolean usePoints) {
        if (!orderStatusList.contains(orderState)) {
            throw new APIException(ResultCodeEnum.ORDER_STATUS_UNMATCHED);
        }
        Order order = ordersMapper.getOrderById(orderId);
        Long userId = getCurrentUserId();
        if (null == order) {
            throw new APIException(ResultCodeEnum.ORDER_MISSED);
        }

        Business business = businessClient.gainBusinessById(order.getBusinessId()).getData();

        // 订单状态(0-待支付,1-待接单,2-已接单,3-已完成,4-已取消)
        if (orderState == 1) {
            if (order.getOrderState() != 0 || !Objects.equals(userId, order.getCustomerId())) {
                throw new APIException(ResultCodeEnum.ORDER_PAY_FAILED);
            }

            // 检查是否是积分兑换订单，积分兑换订单的积分已在兑换时扣除，不需要再次处理
            if (isPointsExchangeOrder(orderId)) {
                // 积分兑换订单不需要处理积分冻结，直接跳过
                log.info("订单{}是积分兑换订单，跳过积分冻结处理", orderId);
            } else {
                // 积分+现金支付：根据usePoints参数决定是否使用积分抵扣
                // usePoints=true 或 null（默认）：使用积分抵扣
                // usePoints=false：不使用积分，只用现金支付
                try {
                // 判断是否使用积分（默认使用积分）
                boolean shouldUsePoints = (usePoints == null || usePoints);

                if (shouldUsePoints) {
                    // 1. 计算可抵扣金额
                    BigDecimal deductibleAmount = pointClient.calculateDeductibleAmount(
                        order.getCustomerId(), order.getOrderTotal()).getData();

                    // 2. 如果可抵扣金额大于0，则冻结积分
                    if (deductibleAmount.compareTo(BigDecimal.ZERO) > 0) {
                    // 获取积分兑换比例
                    BigDecimal exchangeRatio = pointClient.getExchangeRatio().getData();
                    if (exchangeRatio == null || exchangeRatio.compareTo(BigDecimal.ZERO) <= 0) {
                        exchangeRatio = BigDecimal.valueOf(100); // 默认100积分=1元
                    }

                    // 计算需要冻结的积分数量
                    Long pointsToFreeze = deductibleAmount.multiply(exchangeRatio).longValue();

                    // 冻结积分（优先扣减即将过期的积分）
                    pointClient.freezePoints(order.getCustomerId(), pointsToFreeze, orderId);

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
            }

            // 订单支付完成，发送消息到RabbitMQ（异步通知营销系统）
            // 注意：积分兑换订单不需要发送消息，因为积分已扣除，不需要再发放奖励积分
            if (!isPointsExchangeOrder(orderId)) {
                sendOrderPaidMessage(orderId, order);
            }
        }
        if (orderState == 2) {
            if (order.getOrderState() != 1 || !Objects.equals(business.getUserId(),userId)) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }
        }
        if (orderState == 3) {
            if ((null == order.getPaymentMethod() || (order.getPaymentMethod()!= 2)) && (order.getOrderState() != 2 || (!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId)))) {
                throw new APIException(ResultCodeEnum.ORDER_ACCEPT_FAILED);
            }

            if (null != order.getPaymentMethod() && order.getPaymentMethod() == 2) {
                // 商家用户钱包
                paymentClient.income(business.getUserId(),orderId);
                // 交易金额解冻
                paymentClient.thaw(orderId, 0);
            }

            // 订单完成，处理积分相关逻辑
            try {
                // 检查是否是积分兑换订单
                if (isPointsExchangeOrder(orderId)) {
                    // 积分兑换订单：更新积分兑换订单状态为已完成
                    PointsExchangeOrder exchangeOrder = pointClient.gainByOrderId(orderId).getData();
                    if (exchangeOrder != null) {
                        pointClient.updateOrderStatusFromPoint(exchangeOrder.getId(), 1); // 1-已完成
                        log.info("订单{}完成：积分兑换订单状态已更新为已完成", orderId);
                    }
                } else {
                    // 普通订单：处理积分相关逻辑
                    // 1. 真正扣除用户支付的积分（如果使用了积分+现金支付）
                    if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                        pointClient.deductFrozenPoints(order.getCustomerId(), orderId);
                        log.info("订单{}完成：扣除积分{}", orderId, order.getPointsUsed());
                    }

                    // 2. 解冻奖励积分（订单支付时已发放但冻结，现在解冻）
                    pointClient.unfreezeRewardPoints(order.getCustomerId(), orderId);
                }
            } catch (Exception e) {
                // 积分处理失败不影响订单完成，记录日志
                log.error("订单{}完成时积分处理失败: {}", orderId, e.getMessage());
            }
        }
        if (orderState == 4) {
            // 检查是否是积分兑换订单，积分兑换订单不允许取消
            if (isPointsExchangeOrder(orderId)) {
                throw new APIException("POINTS_EXCHANGE_ORDER_CANNOT_CANCEL", "积分兑换订单不允许取消");
            }

            if (order.getOrderState() != 0 && order.getOrderState() != 1) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_DENY);
            }
            if ((!Objects.equals(business.getUserId(),userId) && !Objects.equals(order.getCustomerId(),userId))) {
                throw new APIException(ResultCodeEnum.ORDER_CANCEL_FAILED);
            }

            if (null != order.getPaymentMethod() && order.getPaymentMethod() == 2) {

                // 该订单支付交易永久冻结标记
                paymentClient.thaw(orderId, 3);

                // 退款交易
                TransactionDTO backTransaction = new TransactionDTO(userId, business.getUserId(), orderId);

                // 钱包退款到账
                paymentClient.back(backTransaction);
            }

            // 订单取消，取消奖励积分（如果已发放）和解冻用户支付的积分（如果使用了积分+现金）
            try {
                // 取消奖励积分（如果订单支付时已发放）
                pointClient.cancelRewardPoints(order.getCustomerId(), orderId);
                // 解冻用户支付的积分（如果使用了积分+现金支付）
                if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                    pointClient.unfreezePointsCanceled(order.getCustomerId(), orderId);
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
            Business business1 = businessClient.gainBusinessById(order1.getBusinessId()).getData();
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
        PointsExchangeOrder exchangeOrder = pointClient.gainByOrderId(orderId).getData();
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
        } catch (Exception e) {
            // 消息发送失败不影响订单状态更新
            // 可以考虑记录日志或发送到死信队列
            // 这里暂时只记录日志
            System.err.println("发送订单支付完成消息失败: orderId=" + orderId + ", error=" + e.getMessage());
        }
    }

    @Override
    public boolean sendPaidMessage(Long orderId) {
        Order order = ordersMapper.getOrderById(orderId);
        sendOrderPaidMessage(orderId, order);
        return true;
    }

    @Override
    @Transactional
    public Long orderSubmit(Long businessId,Long addressId) {
        log.info("📦 ========== 开始提交订单 ==========");
        log.info("📦 businessId: {}", businessId);
        log.info("📦 addressId: {}", addressId);
        
        // 商家是否存在
        Business business = businessClient.gainBusinessById(businessId).getData();
        if (null == business) {
            log.error("❌ 商家不存在: {}", businessId);
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }
        log.info("✅ 商家信息获取成功: {}", business.getBusinessName());

        // 设置订单信息
        Long userId = getCurrentUserId();
        log.info("👤 当前用户ID: {}", userId);
        
        List<CartItemVO> cartItemsInBusiness = foodClient.listCartItem(businessId).getData();
        log.info("🛒 购物车商品数量: {}", cartItemsInBusiness.size());
        
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

        DeliveryAddress deliveryAddress = userClient.gainDeliveryAddressById(addressId).getData();
        if (null == deliveryAddress) {
            log.error("❌ 地址不存在: {}", addressId);
            throw new APIException(ResultCodeEnum.ADDRESS_MISSED);
        }
        log.info("✅ 配送地址获取成功: {}", deliveryAddress.getAddress());
        
        order.setAddress(deliveryAddress.getAddress());
        order.setContactName(deliveryAddress.getContactName());
        order.setContactSex(deliveryAddress.getContactSex());
        order.setContactTel(deliveryAddress.getContactTel());

        // 计算总价
        double totalPrice = 0.0;
        for (CartItemVO cartItemVO : cartItemsInBusiness) {
            totalPrice += (cartItemVO.getFoodPrice() * cartItemVO.getQuantity());
        }

        if (totalPrice == 0.0) {
            log.error("❌ 订单总价为0");
            throw new APIException(ResultCodeEnum.ORDER_SUBMIT_FAILED);
        }
        totalPrice += business.getDeliveryPrice().doubleValue();

        // 浮点数精度,保留两位小数
        BigDecimal price = BigDecimal.valueOf(totalPrice);
        order.setOrderTotal(price.setScale(2, RoundingMode.HALF_UP));
        // 订单保存当前商家配送费,避免商家修改导致的不一致
        order.setDeliveryPrice(business.getDeliveryPrice());
        log.info("💰 订单总价: {}", order.getOrderTotal());

        // 插入订单数据到数据库
        ordersMapper.insertOrderPlus(order);
        log.info("✅ 订单插入成功, 订单ID: {}", order.getId());

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
        log.info("✅ 订单详情插入成功");

        // 清空该用户在当前商家的购物车
        foodClient.clearCart(businessId);
        log.info("✅ 购物车已清空");

        log.info("🎉 ========== 订单提交成功, 返回orderID: {} ==========", order.getId());
        return order.getId();
    }
    
    /**
     * 自动完成订单（系统调用，用于定时任务）
     * 订单支付七天后自动完成
     * 设计原则：封装与抽象 - 封装自动完成逻辑
     */
    @Override
    @Transactional
    public void autoCompleteOrders() {
        log.info("========== 开始自动完成订单（支付7天后） ==========");
        LocalDateTime startTime = LocalDateTime.now();

        // 1. 计算7天前的时间
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        log.info("【时间计算】当前时间: {}, 7天前时间: {}", LocalDateTime.now(), sevenDaysAgo);

        // 2. 查询已支付（orderState=1）且支付时间超过7天的订单
        List<Order> paidOrders = ordersMapper.selectPaidOrdersBeforeTime(1, sevenDaysAgo);
        log.info("【查询结果】查询到 {} 条已支付超过7天的订单", paidOrders.size());

        if (paidOrders.isEmpty()) {
            log.info("【自动完成】没有需要自动完成的订单");
            return;
        }

        // 2. 统计信息
        int totalOrders = 0;
        int completedCount = 0;
        int errorCount = 0;

        // 3. 处理每个订单
        for (Order order : paidOrders) {
            totalOrders++;
            try {
                log.info("【处理订单】订单ID: {}, 用户ID: {}, 商家ID: {}, 订单金额: {}, 支付时间: {}",
                        order.getId(), order.getCustomerId(), order.getBusinessId(),
                        order.getOrderTotal(), order.getOrderDate());

                // 检查订单状态，确保是已支付状态
                if (order.getOrderState() != 1) {
                    log.warn("【跳过订单】订单ID: {} 的状态不是已支付（当前状态: {}），跳过",
                            order.getId(), order.getOrderState());
                    continue;
                }

                // 调用内部方法完成订单（绕过权限检查）
                completeOrderInternal(order.getId(), order);
                completedCount++;
                log.info("【完成成功】订单ID: {} 已自动完成", order.getId());

            } catch (Exception e) {
                errorCount++;
                log.error("【完成失败】订单ID: {} 自动完成失败，错误: {}",
                        order.getId(), e.getMessage(), e);
                // 继续处理下一个订单，不中断整个流程
            }
        }

        // 4. 输出统计信息
        LocalDateTime endTime = LocalDateTime.now();
        long duration = java.time.Duration.between(startTime, endTime).toMillis();
        log.info("========== 自动完成订单任务完成 ==========");
        log.info("【统计信息】总订单数: {}, 成功完成: {}, 失败: {}, 耗时: {}ms",
                totalOrders, completedCount, errorCount, duration);
    }

    @Override
    public boolean create(CreateOrderDTO orderCreateDTO) {
        ordersMapper.insertOrderPlus(orderCreateDTO.getOrder());
        ordersMapper.setOrderPaymentMethod(orderCreateDTO.getOrder().getId(),3);

        orderDetailetMapper.saveOrderDetailPlus(orderCreateDTO.getOrderDetailet());
        return true;
    }

    /**
     * 内部方法：完成订单（系统调用，绕过权限检查）
     * 设计原则：封装与抽象 - 封装订单完成逻辑
     */
    private void completeOrderInternal(Long orderId, Order order) {
        // 1. 处理虚拟钱包支付（如果使用）
        if (order.getPaymentMethod() != null && order.getPaymentMethod() == 2) {
            try {
                Business business = businessClient.gainBusinessById(order.getBusinessId()).getData();
                if (business != null && business.getUserId() != null) {
                    paymentClient.thaw(orderId, 0);
                    paymentClient.income(getCurrentUserId(),orderId);
                    log.info("【钱包处理】订单ID: {} 的虚拟钱包交易已解冻并到账", orderId);
                }
            } catch (Exception e) {
                log.error("【钱包处理失败】订单ID: {} 的虚拟钱包处理失败: {}", orderId, e.getMessage());
                // 钱包处理失败不影响订单完成
            }
        }

        // 2. 处理积分相关逻辑
        try {
            // 检查是否是积分兑换订单
            if (isPointsExchangeOrder(orderId)) {
                // 积分兑换订单：更新积分兑换订单状态为已完成
                PointsExchangeOrder exchangeOrder = pointClient.gainByOrderId(orderId).getData();
                if (exchangeOrder != null) {
                    pointClient.updateOrderStatusFromPoint(exchangeOrder.getId(), 1); // 1-已完成
                    log.info("【积分兑换订单】订单ID: {} 的积分兑换订单状态已更新为已完成", orderId);
                }
            } else {
                // 普通订单：处理积分相关逻辑
                // 1. 真正扣除用户支付的积分（如果使用了积分+现金支付）
                if (order.getPointsUsed() != null && order.getPointsUsed() > 0) {
                    pointClient.deductFrozenPoints(order.getCustomerId(), orderId);
                    log.info("【积分扣除】订单ID: {} 扣除积分{}", orderId, order.getPointsUsed());
                }

                // 2. 解冻奖励积分（订单支付时已发放但冻结，现在解冻）
                pointClient.unfreezeRewardPoints(order.getCustomerId(), orderId);
                log.info("【积分解冻】订单ID: {} 的奖励积分已解冻", orderId);
            }
        } catch (Exception e) {
            // 积分处理失败不影响订单完成，记录日志
            log.error("【积分处理失败】订单ID: {} 的积分处理失败: {}", orderId, e.getMessage());
        }

        // 3. 更新订单状态为已完成
        ordersMapper.setOrderState(orderId, 3);
        log.info("【状态更新】订单ID: {} 的状态已更新为已完成", orderId);
    }



    /**
     * 获取当前用户ID
     */
    private Long getCurrentUserId() {
        return userClient.getUserByName(UserContext.getUsername()).getData().getId();
    }
}
