package com.tju.elm.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.food",
                "filters",
                "com.tju.elm.api.config",
                "exception",
                "result",
                "handler"
        },
        exclude = {
                DataSourceAutoConfiguration.class,
                DataSourceTransactionManagerAutoConfiguration.class,
                HibernateJpaAutoConfiguration.class
        }
)
@Import({config.CacheConfig.class,config.CommonRedisConfig.class,config.DataSourceConfig.class})
//@Import({config.CacheConfig.class,config.CommonRedisConfig.class,config.DataSourceConfig.class,config.JaegerConfig.class})
@MapperScan(
        basePackages = {"com.tju.elm.food.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
@EnableAspectJAutoProxy(exposeProxy = true, proxyTargetClass = true)
public class FoodServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(FoodServiceApplication.class, args);
    }

}