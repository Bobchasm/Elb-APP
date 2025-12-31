package com.tju.elm.business;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.tju.elm.business.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class BusinessServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(BusinessServiceApplication.class, args);
    }

}