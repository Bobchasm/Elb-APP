package com.tju.elm_bk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableScheduling
@MapperScan(
        basePackages = {
                "com.tju.elm_bk.mapper",
                "com.tju.elm_bk.rich.mapper"},
        annotationClass = org.apache.ibatis.annotations.Mapper.class
)
public class ElmBkApplication {

    public static void main(String[] args) {
        // 设置应用默认时区为东八区（Asia/Shanghai）
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        SpringApplication.run(ElmBkApplication.class, args);
    }

}
