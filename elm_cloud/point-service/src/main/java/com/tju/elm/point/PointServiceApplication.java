package com.tju.elm.point;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import java.util.TimeZone;

@SpringBootApplication(
        scanBasePackages = {
                "com.tju.elm.point",
                "config"
        }
)
@MapperScan(
        basePackages = {"com.tju.elm.point.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class PointServiceApplication {

    public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(PointServiceApplication.class, args);
    }

}
