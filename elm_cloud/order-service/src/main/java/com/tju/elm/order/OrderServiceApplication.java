package com.tju.elm.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.tju.elm.order.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class OrderServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(OrderServiceApplication.class, args);
    }

}
