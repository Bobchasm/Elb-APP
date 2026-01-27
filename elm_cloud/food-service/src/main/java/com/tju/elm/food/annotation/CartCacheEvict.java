package com.tju.elm.food.annotation;

import java.lang.annotation.*;

/**
 * 购物车缓存清除注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CartCacheEvict {

    /**
     * 缓存名称
     */
    String cacheName() default "cart";

    /**
     * 用户名的SpEL表达式，默认从UserContext获取
     */
    String username() default "T(utils.UserContext).getUsername()";

    /**
     * 商家ID的SpEL表达式
     * 例如：使用 #businessId 或 #foodMapper.selectFoodBusinessId(#foodId)
     */
    String businessIdSpEL() default "";

    /**
     * 是否清理所有缓存（慎用）
     */
    boolean allEntries() default false;

    /**
     * 是否在方法执行前清理（默认在执行后清理）
     */
    boolean beforeInvocation() default false;
}