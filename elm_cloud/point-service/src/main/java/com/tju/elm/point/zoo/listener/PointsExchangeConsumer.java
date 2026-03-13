package com.tju.elm.point.zoo.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Address;
import com.tju.elm.api.client.FoodClient;
import com.tju.elm.api.client.OrderClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.dto.CreateOrderDTO;
import com.tju.elm.api.po.DeliveryAddress;
import com.tju.elm.api.po.Food;
import com.tju.elm.api.po.Order;
import com.tju.elm.api.po.OrderDetailet;
import com.tju.elm.point.mapper.MarketingPointsExchangeRuleMapper;
import com.tju.elm.point.mapper.PointsExchangeOrderMapper;
import com.tju.elm.point.service.CacheSyncService;
import com.tju.elm.point.service.PointsService;
import com.tju.elm.point.service.RedisLuaService;
import com.tju.elm.point.zoo.config.PointsRabbitMQConfig;
import com.tju.elm.point.zoo.pojo.dto.PointsDeductDTO;

import com.tju.elm.point.zoo.pojo.entity.MarketingPointsExchangeRule;
import com.tju.elm.point.zoo.pojo.entity.PointsExchangeOrder;
import com.tju.elm.point.zoo.pojo.event.PointsExchangeMessageDTO;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import result.HttpResult;
import result.ResultCodeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.tju.elm.point.zoo.config.PointsRabbitMQConfig.*;

/**
 * 积分兑换消息消费者
 */
@Slf4j
@Component
public class PointsExchangeConsumer {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;

    @Autowired
    private PointsExchangeOrderMapper exchangeOrderMapper;

    @Autowired
    private PointsService pointsService;

    @Autowired
    private FoodClient foodClient;

    @Autowired
    private UserClient userClient;

    @Autowired
    private OrderClient orderClient;

    @Autowired
    private RedisLuaService redisLuaService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CacheSyncService cacheSyncService;

    /**
     * 处理积分兑换消息
     */
    @RabbitListener(
            queues = PointsRabbitMQConfig.POINTS_EXCHANGE_QUEUE,
            containerFactory = "pointsListenerContainerFactory"  // 指定points服务专用的工厂
    )
    @Transactional(rollbackFor = Exception.class)
    public void handlePointsExchange(String messageBody,
                                     @Header(value = "x-correlation-id", required = false) String correlationId) {
        PointsExchangeMessageDTO message = null;

        try {
            // 1.解析消息
            message = objectMapper.readValue(messageBody, PointsExchangeMessageDTO.class);
            String requestId = message.getRequestId();

            log.info("开始处理积分兑换: requestId={}, userId={}, foodId={}",
                    requestId, message.getUserId(), message.getFoodId());

            // 2.幂等性检查（防止重复处理）
            if (isMessageProcessed(requestId)) {
                log.warn("消息已处理过，跳过: requestId={}", requestId);
                return;
            }

            // 3.验证配送地址
            DeliveryAddress address = validateDeliveryAddress(message);

            // 4.查询商品信息
            Food food = getFoodInfo(message.getFoodId());

            // 5.查询兑换规则
            MarketingPointsExchangeRule rule = exchangeRuleMapper.selectByFoodId(message.getFoodId());
            if (rule == null) {
                throw new APIException("EXCHANGE_RULE_NOT_FOUND", "兑换规则不存在");
            }

            // 6.校验积分
            Long availablePoints = pointsService.getPointsAccount(message.getUserId()).getAvailablePoints();
            if (availablePoints < message.getRequiredPoints()) {
                throw new APIException("POINTS_INSUFFICIENT", "积分不足");
            }

            // 7.数据库减库存（使用乐观锁）
            decreaseStockInDB(message.getFoodId(), message.getQuantity(), requestId);

            // 8.创建普通订单
            Order order = new Order();
            order.setBusinessId(food.getBusinessId());
            order.setCustomerId(message.getUserId());
            order.setAddressId(message.getAddressId());
            order.setOrderDate(LocalDateTime.now());
            order.setOrderState(1); // 1-已支付（积分已扣除，相当于已支付）
            order.setOrderTotal(BigDecimal.ZERO); // 积分兑换，订单金额为0
            order.setDeliveryPrice(BigDecimal.ZERO); // 积分兑换商品免配送费
            order.setPaymentMethod(3); // 3-积分兑换（新增支付方式）
            order.setPointsUsed(message.getRequiredPoints()); // 使用的积分数量
            order.setPointsAmount(0L); // 积分兑换不获得积分
            order.setPointsDiscountAmount(BigDecimal.ZERO);
            order.setCreator(message.getUserId());
            order.setUpdater(message.getUserId());
            order.setCreateTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            order.setIsDeleted(false);
            order.setAddress(address.getAddress());
            order.setContactName(address.getContactName());
            order.setContactTel(address.getContactTel());
            order.setContactSex(address.getContactSex());


            // 9.创建订单详情
            OrderDetailet orderDetailet = new OrderDetailet();
            orderDetailet.setOrderId(order.getId());
            orderDetailet.setFoodId(message.getFoodId());
            orderDetailet.setQuantity(message.getQuantity());
            orderDetailet.setFoodPrice(food.getFoodPrice()); // 保存商品原价
            orderDetailet.setCreator(message.getUserId());
            orderDetailet.setUpdater(message.getUserId());
            orderDetailet.setCreateTime(LocalDateTime.now());
            orderDetailet.setUpdateTime(LocalDateTime.now());
            orderDetailet.setIsDeleted(false);


            CreateOrderDTO request = new CreateOrderDTO();
            request.setOrder(order);
            request.setOrderDetailet(orderDetailet);
            HttpResult<Boolean> createResult = orderClient.orderCreate(request);

            if (createResult == null) {
                throw new APIException("ORDER_SERVICE_UNAVAILABLE", "订单服务不可用");
            }

            // 10.扣减积分（库存已减少，如果积分不足，库存会自动回滚）
            PointsDeductDTO deductDTO = new PointsDeductDTO();
            deductDTO.setRelatedOrderId(order.getId());
            deductDTO.setUserId(message.getUserId());
            deductDTO.setPoints(message.getRequiredPoints());
            deductDTO.setPointsSource(4); // 4-兑换商品
            deductDTO.setRelatedFoodId(message.getFoodId());
            deductDTO.setDescription("兑换商品：" + food.getFoodName() + " x" + message.getQuantity());
            pointsService.deductPoints(deductDTO,message.getUserId());

            // 11.创建积分兑换订单记录
            PointsExchangeOrder exchangeOrder = new PointsExchangeOrder();
            exchangeOrder.setUserId(message.getFoodId());
            exchangeOrder.setOrderId(order.getId()); // 关联普通订单
            exchangeOrder.setFoodId(message.getFoodId());
            exchangeOrder.setPointsUsed(message.getRequiredPoints());
            exchangeOrder.setCashAmount(BigDecimal.ZERO); // 纯积分兑换，现金为0
            exchangeOrder.setStatus(0); // 0-待处理（对应订单状态1-已支付）
            exchangeOrder.setCreateTime(LocalDateTime.now());
            exchangeOrder.setUpdateTime(LocalDateTime.now());
            exchangeOrder.setIsDeleted(false);
            exchangeOrderMapper.insert(exchangeOrder);

            // 12.标记消息已处理
            markMessageAsProcessed(requestId);

            // 清理Redis预扣记录（保留一段时间用于查询）
            // redisTemplate.delete("points:exchange:record:" + requestId);

            MarketingPointsExchangeRule updatedRule = exchangeRuleMapper.selectByFoodId(message.getFoodId());
            if (updatedRule != null) {
                // 同步缓存
                cacheSyncService.syncCacheAfterExchange(
                        message.getFoodId(),
                        updatedRule.getStockQuantity()
                );
            }

//            log.info("积分兑换处理完成: requestId={}, orderId={}", requestId, order.getId());

        } catch (Exception e) {
            log.error("处理积分兑换消息失败: correlationId={}", correlationId, e);

            if (message != null && message.getRetryCount() < 3) {
                retryMessage(message, e);
            } else {
                // 重试次数超限，发送到死信队列
                sendToDLQ(messageBody, e.getMessage());
                // 回滚Redis预扣
                if (message != null) {
                    rollbackStock(message);
                }
            }
            throw new RuntimeException(e);
        }
    }

    /**
     * 验证配送地址
     */
    private DeliveryAddress validateDeliveryAddress(PointsExchangeMessageDTO message) {
        DeliveryAddress address = userClient.gainDeliveryAddressById(message.getAddressId()).getData();
        log.info(String.valueOf(address.getUserId().equals(message.getUserId())));
        if (address == null || !address.getUserId().equals(message.getUserId())) {
            throw new APIException(ResultCodeEnum.ADDRESS_MISSED);
        }
        return address;
    }

    /**
     * 查询商品信息
     */
    private Food getFoodInfo(Long foodId) {
        Food food = foodClient.gainFoodId(foodId).getData();
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }
        return food;
    }

    /**
     * 数据库减库存（使用乐观锁）
     */
    private void decreaseStockInDB(Long foodId, Integer quantity, String requestId) {
        // 先查询规则
        MarketingPointsExchangeRule rule = exchangeRuleMapper.selectByFoodId(foodId);
        if (rule == null) {
            throw new APIException("EXCHANGE_RULE_NOT_FOUND", "兑换规则不存在");
        }

        // 使用乐观锁更新库存
        int updateCount = exchangeRuleMapper.decreaseStockWithVersion(
                rule.getId(), quantity);

        if (updateCount == 0) {
            // 乐观锁更新失败，可能是库存不足
            // 重新查询当前库存
            MarketingPointsExchangeRule currentRule = exchangeRuleMapper.selectById(rule.getId());
            if (currentRule.getStockQuantity() < quantity) {
                throw new APIException("STOCK_INSUFFICIENT", "数据库库存不足");
            }
        }

//        log.debug("数据库减库存成功: foodId={}, quantity={}, requestId={}",
//                foodId, quantity, requestId);
    }


    /**
     * 检查消息是否已处理
     */
    private boolean isMessageProcessed(String requestId) {
        String processedKey = "points:exchange:processed:" + requestId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(processedKey));
    }

    /**
     * 标记消息已处理
     */
    private void markMessageAsProcessed(String requestId) {
        String processedKey = "points:exchange:processed:" + requestId;
        redisTemplate.opsForValue().set(processedKey, "1", java.time.Duration.ofMinutes(30));
    }

    /**
     * 重试消息
     */
    private void retryMessage(PointsExchangeMessageDTO message, Exception e) {
        try {
            message.incrementRetryCount();
            String messageBody = objectMapper.writeValueAsString(message);

            // 延迟重试（5秒、10秒、30秒）
            long delay = 5000L * (long) Math.pow(2, message.getRetryCount() - 1);

            rabbitTemplate.convertAndSend(
                    POINTS_EXCHANGE_TOPIC_EXCHANGE,
                    "points.exchange.retry",
                    messageBody,
                    m -> {
                        m.getMessageProperties().setDelay((int) delay);
                        m.getMessageProperties().setCorrelationId(message.getRequestId());
                        return m;
                    }
            );

            log.warn("消息重试: requestId={}, retryCount={}, delay={}ms",
                    message.getRequestId(), message.getRetryCount(), delay);

        } catch (Exception ex) {
            log.error("重试消息失败: requestId={}", message.getRequestId(), ex);
        }
    }

    /**
     * 发送到死信队列
     */
    private void sendToDLQ(String messageBody, String errorReason) {
        try {
            rabbitTemplate.convertAndSend("", PointsRabbitMQConfig.POINTS_EXCHANGE_DLQ, messageBody);
            log.error("消息发送到死信队列: errorReason={}", errorReason);
        } catch (Exception e) {
            log.error("发送到死信队列失败", e);
        }
    }

    /**
     * 回滚Redis预扣
     */
    private void rollbackStock(PointsExchangeMessageDTO message) {
        try {
            redisLuaService.rollbackStock(
                    message.getFoodId(),
                    message.getQuantity(),
                    message.getRequestId()
            );
            log.info("回滚Redis库存成功: requestId={}, foodId={}",
                    message.getRequestId(), message.getFoodId());
        } catch (Exception e) {
            log.error("回滚Redis库存失败: requestId={}", message.getRequestId(), e);
        }
    }

    @Autowired
    private org.springframework.data.redis.core.RedisTemplate<String, Object> redisTemplate;
}