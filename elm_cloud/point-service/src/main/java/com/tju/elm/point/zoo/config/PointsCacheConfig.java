package com.tju.elm.point.zoo.config;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * 积分系统缓存注解配置
 * 自定义缓存key生成策略
 */
@Configuration
public class PointsCacheConfig extends CachingConfigurerSupport {

    /**
     * 积分系统自定义缓存key生成器
     * 格式：类名:方法名:参数值
     */
    @Bean("pointsKeyGenerator")
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuilder sb = new StringBuilder();
            sb.append(target.getClass().getSimpleName());
            sb.append(":");
            sb.append(method.getName());
            for (Object param : params) {
                if (param != null) {
                    sb.append(":");
                    sb.append(param.toString());
                }
            }
            return sb.toString();
        };
    }

    /**
     * 简单key生成器（只使用第一个参数）
     */
    @Bean("simpleKeyGenerator")
    public KeyGenerator simpleKeyGenerator() {
        return (target, method, params) -> {
            if (params.length == 0) {
                return method.getName();
            }
            return params[0] != null ? params[0].toString() : method.getName();
        };
    }
}