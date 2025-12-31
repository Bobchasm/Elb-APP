package com.tju.elm.ai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
@MapperScan(
        basePackages = {"com.tju.elm.ai.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class AiServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(AiServiceApplication.class, args);
    }

}
