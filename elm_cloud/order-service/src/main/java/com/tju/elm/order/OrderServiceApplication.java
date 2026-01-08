package com.tju.elm.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.order",
                "filters",
                "com.tju.elm.api.config",
                "config",
                "exception",
                "result"
        }
)
@MapperScan(
        basePackages = {"com.tju.elm.order.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
public class OrderServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
