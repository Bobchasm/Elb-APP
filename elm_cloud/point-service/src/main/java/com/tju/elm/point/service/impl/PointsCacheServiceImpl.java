package com.tju.elm.point.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tju.elm.point.service.PointsCacheService;
import com.tju.elm.point.zoo.pojo.vo.PointsAccountVO;
import com.tju.elm.point.zoo.pojo.vo.PointsExpirationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 积分缓存服务实现类
 * 职责：管理积分相关的 Redis 缓存
 * 设计原则：单一职责原则 - 只负责缓存操作
 */
@Slf4j
@Service
public class PointsCacheServiceImpl implements PointsCacheService {

    @Autowired
    @Qualifier("pointsRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    
    // 配置 ObjectMapper 支持 LocalDateTime
    private static final ObjectMapper objectMapper = new ObjectMapper();
    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
    
    // 缓存 Key 前缀
    private static final String CACHE_KEY_ACCOUNT = "points:account:";
    private static final String CACHE_KEY_EXCHANGE_RATIO = "points:exchange:ratio";
    private static final String CACHE_KEY_EXPIRING = "points:expiring:";
    
    // 缓存过期时间（秒）
    private static final long CACHE_EXPIRE_ACCOUNT = 300; // 5分钟
    private static final long CACHE_EXPIRE_EXCHANGE_RATIO = 1800; // 30分钟
    private static final long CACHE_EXPIRE_EXPIRING = 300; // 5分钟

    @Override
    public PointsAccountVO getAccountCache(Long userId) {
        try {
            String key = CACHE_KEY_ACCOUNT + userId;
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                return objectMapper.convertValue(value, PointsAccountVO.class);
            }
        } catch (Exception e) {
            log.warn("获取积分账户缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
        return null;
    }

    @Override
    public void setAccountCache(Long userId, PointsAccountVO account) {
        try {
            String key = CACHE_KEY_ACCOUNT + userId;
            redisTemplate.opsForValue().set(key, account, CACHE_EXPIRE_ACCOUNT, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("设置积分账户缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public void deleteAccountCache(Long userId) {
        try {
            String key = CACHE_KEY_ACCOUNT + userId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("删除积分账户缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public BigDecimal getExchangeRatioCache() {
        try {
            Object value = redisTemplate.opsForValue().get(CACHE_KEY_EXCHANGE_RATIO);
            if (value != null) {
                if (value instanceof Number) {
                    return BigDecimal.valueOf(((Number) value).doubleValue());
                } else if (value instanceof String) {
                    return new BigDecimal((String) value);
                }
            }
        } catch (Exception e) {
            log.warn("获取积分兑换比例缓存失败，error: {}", e.getMessage());
        }
        return null;
    }

    @Override
    public void setExchangeRatioCache(BigDecimal ratio) {
        try {
            redisTemplate.opsForValue().set(CACHE_KEY_EXCHANGE_RATIO, ratio, 
                CACHE_EXPIRE_EXCHANGE_RATIO, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("设置积分兑换比例缓存失败，error: {}", e.getMessage());
        }
    }

    @Override
    public void deleteExchangeRatioCache() {
        try {
            redisTemplate.delete(CACHE_KEY_EXCHANGE_RATIO);
        } catch (Exception e) {
            log.warn("删除积分兑换比例缓存失败，error: {}", e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<?> getExpiringPointsCache(Long userId) {
        try {
            String key = CACHE_KEY_EXPIRING + userId;
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                // Redis 返回的是 List，直接转换
                if (value instanceof List) {
                    return (List<?>) value;
                }
                // 如果不是 List，尝试用 ObjectMapper 转换
                return objectMapper.convertValue(value, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, PointsExpirationVO.class));
            }
        } catch (Exception e) {
            log.warn("获取即将过期积分缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
        return null;
    }

    @Override
    public void setExpiringPointsCache(Long userId, List<?> expiringPoints) {
        try {
            String key = CACHE_KEY_EXPIRING + userId;
            redisTemplate.opsForValue().set(key, expiringPoints, CACHE_EXPIRE_EXPIRING, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("设置即将过期积分缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public void deleteExpiringPointsCache(Long userId) {
        try {
            String key = CACHE_KEY_EXPIRING + userId;
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.warn("删除即将过期积分缓存失败，userId: {}, error: {}", userId, e.getMessage());
        }
    }

    @Override
    public void deleteUserAllCache(Long userId) {
        deleteAccountCache(userId);
        deleteExpiringPointsCache(userId);
    }
}

