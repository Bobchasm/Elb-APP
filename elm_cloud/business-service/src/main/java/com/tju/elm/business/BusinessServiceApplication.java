package com.tju.elm.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.business",
                "filters",
                "com.tju.elm.api.config"
        }
)
@MapperScan(
        basePackages = {"com.tju.elm.business.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.tju.elm.api.client")
public class BusinessServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BusinessServiceApplication.class, args);
    }

}