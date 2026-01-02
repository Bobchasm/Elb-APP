package com.tju.elm.food;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.food",
                "filters",
                "com.tju.elm.api.config"
        }
)
@MapperScan(
        basePackages = {"com.tju.elm.food.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
public class FoodServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(FoodServiceApplication.class, args);
    }

}