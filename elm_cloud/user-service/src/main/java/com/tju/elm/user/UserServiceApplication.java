package com.tju.elm.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.tju.elm.user.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
@Import({config.RedisConfig.class})
@EnableDiscoveryClient
public class UserServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(UserServiceApplication.class, args);
    }

}
