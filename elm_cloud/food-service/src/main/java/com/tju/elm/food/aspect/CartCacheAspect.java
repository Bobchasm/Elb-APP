package com.tju.elm.food.aspect;

import com.tju.elm.food.annotation.CartCacheEvict;
import com.tju.elm.food.annotation.CartCacheable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import utils.UserContext;

import java.lang.reflect.Method;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
@Order(10)
public class CartCacheAspect {

    private final CacheManager cacheManager;
    private final BeanFactory beanFactory;
    private final SpelExpressionParser spelParser = new SpelExpressionParser();
    private final DefaultParameterNameDiscoverer paramDiscoverer = new DefaultParameterNameDiscoverer();

    /**
     * 处理 @CartCacheable 注解
     */
    @Around("@annotation(cartCacheable)")
    public Object handleCacheable(ProceedingJoinPoint joinPoint, CartCacheable cartCacheable) throws Throwable {
        try {
            Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
            Object[] args = joinPoint.getArgs();

            // 创建支持Bean解析的EvaluationContext
            StandardEvaluationContext context = createEvaluationContext(method, args);

            // 解析SpEL表达式获取用户名和商家ID
            String username = evaluateSpEL(cartCacheable.username(), context, String.class);
            Long businessId = evaluateSpEL(cartCacheable.businessIdSpEL(), context, Long.class);

            // 如果businessId为null，直接执行方法
            if (businessId == null) {
                log.warn("无法获取businessId，跳过缓存查询");
                return joinPoint.proceed();
            }

            // 构建缓存key
            String cacheKey = buildCacheKey(username, businessId);

            // 获取缓存
            Cache cache = cacheManager.getCache(cartCacheable.cacheName());
            if (cache == null) {
                log.warn("缓存 {} 不存在，直接执行方法", cartCacheable.cacheName());
                return joinPoint.proceed();
            }

            // 尝试从缓存获取
            Cache.ValueWrapper cachedValue = cache.get(cacheKey);
            if (cachedValue != null) {
                log.debug("从缓存获取购物车数据: user={}, businessId={}", username, businessId);
                return cachedValue.get();
            }

            // 缓存未命中，执行方法
            Object result = joinPoint.proceed();

            // 将结果放入缓存
            if (result != null) {
                cache.put(cacheKey, result);
                log.debug("购物车数据放入缓存: user={}, businessId={}", username, businessId);
            }

            return result;

        } catch (Exception e) {
            log.error("处理 @CartCacheable 注解时出错: {}", e.getMessage());
            // 出错时直接执行方法
            return joinPoint.proceed();
        }
    }

    /**
     * 处理 @CartCacheEvict 注解
     */
    @Around("@annotation(cartCacheEvict)")
    public Object handleCacheEvict(ProceedingJoinPoint joinPoint, CartCacheEvict cartCacheEvict) throws Throwable {
        // 执行方法
        Object result;
        try {
            result = joinPoint.proceed();
        } catch (Exception e) {
            // 业务方法执行失败，不清理缓存
            log.error("业务方法执行失败，跳过缓存清理: {}", e.getMessage());
            throw e;
        }

        // 业务执行成功后清理缓存（默认在执行后清理）
        if (!cartCacheEvict.beforeInvocation()) {
            evictCacheAfterSuccess(joinPoint, cartCacheEvict);
        }

        return result;
    }

    /**
     * 业务成功后清理缓存
     */
    private void evictCacheAfterSuccess(ProceedingJoinPoint joinPoint, CartCacheEvict cartCacheEvict) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Object[] args = joinPoint.getArgs();

        Cache cache = cacheManager.getCache(cartCacheEvict.cacheName());
        if (cache == null) {
            log.warn("缓存 {} 不存在，无法清理", cartCacheEvict.cacheName());
            return;
        }

        // 清理所有缓存
        if (cartCacheEvict.allEntries()) {
            try {
                cache.clear();
                log.debug("清理所有购物车缓存");
            } catch (Exception e) {
                log.warn("清理所有缓存失败: {}", e.getMessage());
            }
            return;
        }

        try {
            // 创建支持Bean解析的EvaluationContext
            StandardEvaluationContext context = createEvaluationContext(method, args);

            // 解析SpEL表达式获取用户名和商家ID
            String username = evaluateSpEL(cartCacheEvict.username(), context, String.class);
            Long businessId = evaluateSpEL(cartCacheEvict.businessIdSpEL(), context, Long.class);

            // 如果无法获取businessId，记录日志但不清理缓存
            if (businessId == null) {
                log.warn("无法获取businessId，跳过缓存清理: user={}", username);
                return;
            }

            // 构建缓存key
            String cacheKey = buildCacheKey(username, businessId);

            // 清理指定缓存
            cache.evict(cacheKey);
            log.debug("清理购物车缓存: user={}, businessId={}", username, businessId);

        } catch (Exception e) {
            log.warn("清理购物车缓存失败，但不影响业务: {}", e.getMessage());
        }
    }

    /**
     * 创建支持Bean解析的EvaluationContext
     */
    private StandardEvaluationContext createEvaluationContext(Method method, Object[] args) {
        MethodBasedEvaluationContext context = new MethodBasedEvaluationContext(
                null, method, args, paramDiscoverer);

        // 添加Bean解析器
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        // 添加UserContext
        context.setVariable("userContext", UserContext.class);

        return context;
    }

    /**
     * 构建缓存键
     */
    private String buildCacheKey(String username, Long businessId) {
        return String.format("user%sbus%d", username, businessId);
    }

    /**
     * 解析SpEL表达式（泛型）
     */
    private <T> T evaluateSpEL(String expression, StandardEvaluationContext context, Class<T> clazz) {
        if (expression == null || expression.isEmpty()) {
            return null;
        }

        try {
            Expression expr = spelParser.parseExpression(expression);
            Object value = expr.getValue(context);

            return clazz.isInstance(value) ? clazz.cast(value) : null;

        } catch (Exception e) {
            log.warn("解析SpEL表达式失败: {}, 错误: {}", expression, e.getMessage());
            return null;
        }
    }
}