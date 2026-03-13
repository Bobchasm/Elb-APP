package com.tju.elm.point.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tju.elm.point.mapper.MarketingPointsExchangeRuleMapper;
import com.tju.elm.point.zoo.config.PointsRabbitMQConfig;
import com.tju.elm.point.zoo.pojo.dto.PointsExchangeDTO;

import com.tju.elm.point.zoo.pojo.entity.MarketingPointsExchangeRule;
import com.tju.elm.point.zoo.pojo.event.PointsExchangeMessageDTO;
import com.tju.elm.point.zoo.pojo.vo.LuaScriptResult;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 积分兑换
 */
@Slf4j
@Service
public class ExchangeService {

    @Autowired
    private RedisLuaService redisLuaService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;



    // 使用原有的pointsRedisTemplate进行缓存检查
    @Autowired
    @Qualifier("pointsRedisTemplate")
    private org.springframework.data.redis.core.RedisTemplate<String, Object> pointsRedisTemplate;

    /**
     * 预扣
     */
    public String preDeductForExchange(Long userId, PointsExchangeDTO dto) {
        // 生成唯一请求ID
        String requestId = generateRequestId(userId, dto.getFoodId());

        try {
            // 查询兑换规则
            MarketingPointsExchangeRule rule = exchangeRuleMapper.selectByFoodId(dto.getFoodId());
            if (rule == null) {
                throw new APIException("EXCHANGE_RULE_NOT_FOUND", "兑换规则不存在");
            }

            // 验证基础信息
            validateExchangeInfo(rule, dto);

            // 计算所需积分
            Long requiredPoints = rule.getRequiredPoints() * dto.getQuantity();

//            // 预热缓存
//            warmUpCacheIfNeeded(dto.getFoodId(), userId, rule.getStockQuantity());

            // 执行Lua脚本原子预扣
            LuaScriptResult result = redisLuaService.executePointsExchange(
                    dto.getFoodId(), userId, dto.getQuantity(), requiredPoints, requestId);

            log.info("Lua脚本执行结果: {}", result);

            // 处理预扣结果
            if (!result.isSuccess()) {
                switch (result.getCode()) {
                    case -1:
                        throw new APIException("STOCK_INSUFFICIENT", "库存不足");
                    case -2:
                        throw new APIException("POINTS_INSUFFICIENT", "积分不足");
                    case -3:
                        throw new APIException("DUPLICATE_REQUEST", "请勿重复提交");
                    case -4:
                        throw new APIException("INVALID_QUANTITY", "兑换数量必须大于0");
                    case -5:
                        throw new APIException("INVALID_POINTS", "所需积分无效");
                    default:
                        throw new APIException("EXCHANGE_FAILED", "兑换失败，请重试");
                }
            }

            // 发送消息到RabbitMQ
            sendExchangeMessage(userId, dto, rule, requiredPoints, requestId);

            log.info("积分兑换预扣成功: requestId={}, userId={}, foodId={}", requestId, userId, dto.getFoodId());

            return requestId;

        } catch (APIException e) {
            throw e;
        } catch (Exception e) {
            log.error("积分兑换预扣失败: userId={}, foodId={}", userId, dto.getFoodId(), e);
            throw new APIException("EXCHANGE_FAILED", "兑换失败，请重试");
        }
    }

    /**
     * 发送兑换消息到RabbitMQ
     */
    private void sendExchangeMessage(Long userId, PointsExchangeDTO dto,
                                     MarketingPointsExchangeRule rule,
                                     Long requiredPoints, String requestId) {
        try {
            PointsExchangeMessageDTO message = new PointsExchangeMessageDTO();
            message.setRequestId(requestId);
            message.setUserId(userId);
            message.setFoodId(dto.getFoodId());
            message.setQuantity(dto.getQuantity());
            message.setRequiredPoints(requiredPoints);
            message.setAddressId(dto.getAddressId());
            message.setPreDeductTime(LocalDateTime.now());

            String messageBody = objectMapper.writeValueAsString(message);

            rabbitTemplate.convertAndSend(
                    PointsRabbitMQConfig.POINTS_EXCHANGE_TOPIC_EXCHANGE,
                    PointsRabbitMQConfig.POINTS_EXCHANGE_ROUTING_KEY,
                    messageBody,
                    m -> {
                        m.getMessageProperties().setCorrelationId(requestId);
                        m.getMessageProperties().setMessageId(requestId);
                        // 设置消息过期时间（30秒）
                        m.getMessageProperties().setExpiration("30000");
                        return m;
                    }
            );

//            log.debug("发送兑换消息成功: requestId={}", requestId);

        } catch (Exception e) {
//            log.error("发送兑换消息失败: requestId={}", requestId, e);
            // 发送失败需要回滚Redis预扣
            redisLuaService.rollbackStock(
                    dto.getFoodId(),
                    dto.getQuantity(),
                    requestId
            );
            throw new APIException("SYSTEM_ERROR", "系统错误，请重试");
        }
    }

    /**
     * 验证兑换信息
     */
    private void validateExchangeInfo(MarketingPointsExchangeRule rule, PointsExchangeDTO dto) {
        // 检查库存
        if (rule.getStockQuantity() == null || rule.getStockQuantity() < dto.getQuantity()) {
            throw new APIException("STOCK_INSUFFICIENT", "库存不足");
        }

        // 检查规则状态
        if (rule.getRuleStatus() != null && rule.getRuleStatus() == 0) {
            throw new APIException("RULE_DISABLED", "兑换规则已禁用");
        }

        // 验证兑换数量
        if (dto.getQuantity() <= 0) {
            throw new APIException("INVALID_QUANTITY", "兑换数量必须大于0");
        }
    }

//    /**
//     * 预热缓存
//     */
//    private void warmUpCacheIfNeeded(Long foodId, Long userId, Integer stock) {
//        try {
//            // 预热商品库存
//            String stockKey = "points:stock:" + foodId;
//            Boolean hasStockKey = pointsRedisTemplate.hasKey(stockKey);
//            if (hasStockKey == null || !hasStockKey) {
//                redisLuaService.warmUpStock(foodId, stock);
//            }
//
//            // 预热用户积分
//            String pointsKey = "points:balance:" + userId;
//            Boolean hasPointsKey = pointsRedisTemplate.hasKey(pointsKey);
//            if (hasPointsKey == null || !hasPointsKey) {
//                try {
//                    Long availablePoints = pointsService.getPointsAccount(userId).getAvailablePoints();
//                    redisLuaService.warmUpUserPoints(userId, availablePoints);
//                } catch (Exception e) {
//                    log.warn("查询用户积分失败，设置为0: userId={}", userId, e);
//                    redisLuaService.warmUpUserPoints(userId, 0L);
//                }
//            }
//        } catch (Exception e) {
//            log.warn("预热缓存失败: foodId={}, userId={}", foodId, userId, e);
//        }
//    }

    /**
     * 生成唯一请求ID
     */
    private String generateRequestId(Long userId, Long foodId) {
        return String.format("exchange:%d:%d:%s:%d",
                userId, foodId,
                UUID.randomUUID().toString().substring(0, 8),
                System.currentTimeMillis() % 10000
        );
    }

}