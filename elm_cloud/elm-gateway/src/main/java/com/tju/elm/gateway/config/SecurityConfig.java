package com.tju.elm.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .cors().disable()
                .authorizeExchange()
                .anyExchange().permitAll()  // 允许所有请求
                .and()
                .formLogin().disable()  // 禁用登录表单
                .httpBasic().disable()  // 禁用 HTTP Basic
                .logout().disable()     // 禁用注销
                .build();
    }
}