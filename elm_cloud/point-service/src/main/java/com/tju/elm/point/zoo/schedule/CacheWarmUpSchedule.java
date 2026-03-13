package com.tju.elm.point.zoo.schedule;

import com.tju.elm.point.mapper.MarketingPointsExchangeRuleMapper;
import com.tju.elm.point.service.RedisLuaService;
import com.tju.elm.point.zoo.pojo.entity.MarketingPointsExchangeRule;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@EnableScheduling
public class CacheWarmUpSchedule {

    @Autowired
    private RedisLuaService redisLuaService;

    @Autowired
    private MarketingPointsExchangeRuleMapper exchangeRuleMapper;

    /**
     * 应用启动时预热商品库存
     */
    @PostConstruct
    public void warmUpOnStartup() {
        log.info("开始预热商品库存缓存...");

        try {
            List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectExchangeGoodsRules();
            int warmedCount = 0;

            for (MarketingPointsExchangeRule rule : rules) {
                if (rule.getFoodId() != null && rule.getStockQuantity() != null
                        && rule.getRuleStatus() == 1) {
                    redisLuaService.warmUpStock(rule.getFoodId(), rule.getStockQuantity());
                    warmedCount++;
                }
            }

            log.info("商品库存预热完成，共预热 {} 个商品", warmedCount);

        } catch (Exception e) {
            log.error("预热商品库存失败", e);
        }
    }

    /**
     * 定时同步Redis和数据库库存（每10分钟一次）
     */
    @Scheduled(fixedDelay = 600000)  // 10分钟
    public void syncStockCache() {
        log.debug("开始同步库存缓存...");

        try {
            List<MarketingPointsExchangeRule> rules = exchangeRuleMapper.selectExchangeGoodsRules();
            int syncedCount = 0;

            for (MarketingPointsExchangeRule rule : rules) {
                if (rule.getFoodId() != null && rule.getStockQuantity() != null) {
                    // 直接用数据库值覆盖Redis
                    redisLuaService.warmUpStock(rule.getFoodId(), rule.getStockQuantity());
                    syncedCount++;
                }
            }

            log.debug("库存缓存同步完成，共同步 {} 个商品", syncedCount);

        } catch (Exception e) {
            log.error("同步库存缓存失败", e);
        }
    }
}