package com.tju.elm.point.zoo.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
@Slf4j
public class RedisConfig {

    /**
     * 积分系统专用
     */
    @Bean(name = "pointsRedisTemplate")
    @Primary  // 设置为主要的，确保注入时优先使用这个
    public RedisTemplate<String, Object> pointsRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = getPointsJacksonSerializer();

        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);

        template.afterPropertiesSet();

        log.info("积分系统专用RedisTemplate初始化完成");
        return template;
    }

    /**
     * 通用RedisTemplate
     * 与common模块相同的序列化方式，注解缓存兼容
     */
    @Bean(name = "commonRedisTemplate")
    @ConditionalOnMissingBean(name = "commonRedisTemplate")  // 如果common模块已经配置，则不重复创建
    public RedisTemplate<String, Object> commonRedisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("初始化积分系统通用RedisTemplate（兼容common配置）");

        return createCommonRedisTemplate(connectionFactory);
    }

    /**
     * 积分系统专用的缓存配置
     * 用于@Cacheable等注解，使用原有序列化方式
     */
    @Bean(name = "pointsCacheConfiguration")
    public RedisCacheConfiguration pointsCacheConfiguration() {
        Jackson2JsonRedisSerializer<Object> serializer = getPointsJacksonSerializer();

        return RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()
                ))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        serializer
                ))
                .entryTtl(Duration.ofMinutes(30))  // 积分系统默认30分钟
                .computePrefixWith(cacheName -> "points:" + cacheName + ":")  // 积分系统前缀
                .disableCachingNullValues();
    }

    /**
     * 积分系统专用缓存管理器（用于注解）
     */
    @Bean(name = "pointsCacheManager")
    @Primary  // 这是积分系统主要的缓存管理器
    public CacheManager pointsCacheManager(
            RedisConnectionFactory redisConnectionFactory,
            RedisCacheConfiguration pointsCacheConfiguration) {

        RedisCacheManager manager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(pointsCacheConfiguration)
                .transactionAware()
                .build();

        log.info("积分系统专用缓存管理器初始化完成（前缀：points:）");
        return manager;
    }

    /**
     * 通用缓存管理器（与common模块一致）
     * 如果不指定cacheManager，默认会使用这个
     */
    @Bean(name = "commonCacheManager")
    public CacheManager commonCacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 创建与common模块相同的配置
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.activateDefaultTyping(
                om.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new StringRedisSerializer()
                ))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(om, Object.class)
                ))
                .entryTtl(Duration.ofMinutes(10))
                .computePrefixWith(cacheName -> "elm:points:" + cacheName + ":")  // 加points前缀区分
                .disableCachingNullValues();

        RedisCacheManager manager = RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(config)
                .transactionAware()
                .build();

        log.info("积分系统通用缓存管理器初始化完成（兼容common配置）");
        return manager;
    }

    /**
     * 获取积分系统专用的Jackson序列化器
     */
    private Jackson2JsonRedisSerializer<Object> getPointsJacksonSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(om.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL);
        om.registerModule(new JavaTimeModule());
        om.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        return jackson2JsonRedisSerializer;
    }

    /**
     * 创建与common模块兼容的RedisTemplate
     */
    private RedisTemplate<String, Object> createCommonRedisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 使用common模块相同的序列化配置
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());
        om.activateDefaultTyping(
                om.getPolymorphicTypeValidator(),
                ObjectMapper.DefaultTyping.NON_FINAL
        );

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(om, Object.class);
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
}