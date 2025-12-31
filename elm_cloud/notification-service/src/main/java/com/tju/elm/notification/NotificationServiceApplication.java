package com.tju.elm.notification;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.tju.elm.notification.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class NotificationServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(NotificationServiceApplication.class, args);
    }

}
