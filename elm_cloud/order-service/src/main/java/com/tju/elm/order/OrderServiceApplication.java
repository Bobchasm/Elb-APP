package com.tju.elm.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.order",
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
@Import({config.CacheConfig.class,config.DataSourceConfig.class,config.CommonRedisConfig.class,config.RabbitMQConfig.class,com.tju.elm.api.config.DefaultFeignConfig.class, com.tju.elm.api.config.FeignErrorDecoder.class,com.tju.elm.api.client.fallback.OrderClientFallbackFactory.class})
//@Import({config.CacheConfig.class,config.DataSourceConfig.class,config.CommonRedisConfig.class,config.RabbitMQConfig.class,config.JaegerConfig.class,com.tju.elm.api.config.DefaultFeignConfig.class, com.tju.elm.api.config.FeignErrorDecoder.class,com.tju.elm.api.client.fallback.OrderClientFallbackFactory.class})
@MapperScan(
        basePackages = {"com.tju.elm.order.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
@EnableScheduling  // 启用定时任务
public class OrderServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
