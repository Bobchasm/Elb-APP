package com.tju.elm.point.service;

import com.tju.elm.point.mapper.MarketingPointsExchangeRuleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


/**
 * 缓存同步
 */
@Slf4j
@Service
public class CacheSyncService {

    @Autowired
    private CacheManager pointsCacheManager;

    @Autowired
    @Qualifier("pointsRedisTemplate")
    private RedisTemplate<String, Object> pointsRedisTemplate;

    @Autowired
    @Qualifier("luaObjectRedisTemplate")
    private RedisTemplate<String, Object> luaRedisTemplate;

    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;

    /**
     * 清理商品列表缓存
     */
    public void clearGoodsListCache() {
        try {
            Cache cache = pointsCacheManager.getCache("exchange");
            if (cache != null) {
                cache.evict("good_list");
                log.info("清理商品列表缓存成功");
            }
        } catch (Exception e) {
            log.error("清理商品列表缓存失败", e);
        }
    }

    /**
     * 清理特定商品的详情缓存
     */
    public void clearGoodsDetailCache(Long foodId) {
        try {
            Cache cache = pointsCacheManager.getCache("exchange");
            if (cache != null) {
                cache.evict("good_detail:" + foodId);
            }
        } catch (Exception e) {
            log.error("清理商品详情缓存失败: foodId={}", foodId, e);
        }
    }


    /**
     * 兑换成功后调用：同步所有相关缓存
     */
    public void syncCacheAfterExchange(Long foodId, Integer newStock) {
        try {
            // 更新Lua脚本的库存缓存
            String stockKey = "points:stock:" + foodId;
            luaRedisTemplate.opsForValue().set(stockKey, newStock.toString());

            // 清理Spring Cache的商品列表缓存
            clearGoodsListCache();

            // 清理特定商品的详情缓存
            clearGoodsDetailCache(foodId);

            log.info("兑换后缓存同步完成: foodId={}, newStock={}", foodId, newStock);

        } catch (Exception e) {
            log.error("兑换后缓存同步失败: foodId={}", foodId, e);
        }
    }
}