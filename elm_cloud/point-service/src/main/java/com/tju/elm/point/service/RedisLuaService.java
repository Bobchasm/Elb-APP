package com.tju.elm.point.service;

import com.tju.elm.point.zoo.pojo.vo.LuaScriptResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;

/**
 * Lua脚本执行
 */
@Slf4j
@Service
public class RedisLuaService {

    @Autowired
    @Qualifier("luaObjectRedisTemplate")
    private RedisTemplate<String, Object> luaRedisTemplate;

    private static final DefaultRedisScript<String> POINTS_EXCHANGE_SCRIPT;

    static {
        POINTS_EXCHANGE_SCRIPT = new DefaultRedisScript<>();
        POINTS_EXCHANGE_SCRIPT.setLocation(new ClassPathResource("lua/points-exchange.lua"));
        POINTS_EXCHANGE_SCRIPT.setResultType(String.class);
    }

    /**
     * 执行积分兑换预扣
     */
    public LuaScriptResult executePointsExchange(Long foodId, Long userId,
                                                 Integer quantity, Long requiredPoints,
                                                 String requestId) {
        String stockKey = "points:stock:" + foodId;
        String recordKey = "points:exchange:record:" + requestId;

        try {
//            log.info("执行Lua脚本: foodId={}, userId={}, quantity={}, requiredPoints={}",
//                    foodId, userId, quantity, requiredPoints);

            String rawResult = luaRedisTemplate.execute(
                    POINTS_EXCHANGE_SCRIPT,
                    Arrays.asList(stockKey, recordKey),  // 现在只有2个key
                    foodId.toString(),
                    userId.toString(),
                    quantity.toString(),
                    requiredPoints.toString(),
                    requestId
            );

//            log.info("Lua脚本执行结果: {}", rawResult);

            // 解析结果
            return parseLuaResult(rawResult);

        } catch (Exception e) {
            log.error("执行Lua脚本失败", e);
            throw new RuntimeException("系统繁忙，请重试", e);
        }
    }

    /**
     * 解析Lua脚本结果
     */
    private LuaScriptResult parseLuaResult(String luaResult) {
        try {
            if (luaResult == null || luaResult.trim().isEmpty()) {
                return new LuaScriptResult(-99, "系统错误", null);
            }

            String[] parts = luaResult.split(":", 3);
            if (parts.length < 3) {
                log.error("Lua脚本结果格式错误: {}", luaResult);
                return new LuaScriptResult(-99, "系统错误", null);
            }

            Integer code = Integer.parseInt(parts[0]);
            String message = parts[1];
            String requestId = parts[2];

            return new LuaScriptResult(code, message, requestId);

        } catch (Exception e) {
            log.error("解析Lua脚本结果失败: {}", luaResult, e);
            return new LuaScriptResult(-99, "系统错误", null);
        }
    }

    /**
     * 回滚库存预扣
     */
    public void rollbackStock(Long foodId, Integer quantity, String requestId) {
        try {
            String stockKey = "points:stock:" + foodId;
            String recordKey = "points:exchange:record:" + requestId;

            // 恢复库存
            luaRedisTemplate.opsForValue().increment(stockKey, quantity);
            // 删除记录
            luaRedisTemplate.delete(recordKey);
            // 删除详情
            luaRedisTemplate.delete("points:exchange:detail:" + requestId);

            log.info("回滚库存预扣成功: requestId={}, foodId={}", requestId, foodId);

        } catch (Exception e) {
            log.error("回滚库存预扣失败: requestId={}", requestId, e);
        }
    }

    /**
     * 预热商品库存
     */
    public void warmUpStock(Long foodId, Integer stock) {
        try {
            String stockKey = "points:stock:" + foodId;
            if (stock != null) {
                luaRedisTemplate.opsForValue().set(stockKey, stock.toString());
//                log.info("预热商品库存: foodId={}, stock={}", foodId, stock);
            }
        } catch (Exception e) {
            log.warn("预热商品库存失败: foodId={}", foodId, e);
        }
    }
}