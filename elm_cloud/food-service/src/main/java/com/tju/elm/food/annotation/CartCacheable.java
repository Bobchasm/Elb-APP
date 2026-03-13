package com.tju.elm.food.annotation;

import java.lang.annotation.*;

/**
 * 购物车缓存查询注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CartCacheable {

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
     * 例如：使用 #businessId
     */
    String businessIdSpEL();
}