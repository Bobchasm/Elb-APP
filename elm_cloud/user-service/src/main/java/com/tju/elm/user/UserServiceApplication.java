package com.tju.elm.user;

import lombok.extern.slf4j.Slf4j;
import org.apache.naming.java.javaURLContextFactory;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

@Slf4j
@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.user",
                "filters",
                "com.tju.elm.api.config",
                "result"
        }
)
@Import({config.CacheConfig.class,config.CommonRedisConfig.class})
@MapperScan(
        basePackages = {"com.tju.elm.user.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableCaching
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
public class UserServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
