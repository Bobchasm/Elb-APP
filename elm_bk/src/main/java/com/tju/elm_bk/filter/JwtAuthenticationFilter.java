package com.tju.elm_bk.filter;

import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    //跨域请求允许的域名(Origin头)
    private static final String[] ALLOWED_ORIGINS = new String[]{"*"};

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //配置跨域
        String origin = request.getHeader("Origin");
        for (String allowedOrigin : ALLOWED_ORIGINS) {
            if (allowedOrigin.equals(origin)) {
                response.setHeader("Access-Control-Allow-Origin", allowedOrigin);
                response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, PATCH");
                response.setHeader("Access-Control-Max-Age", "3600");
                response.setHeader("Access-Control-Allow-Headers", "access-control-allow-origin, authority, content-type, version-info, X-Requested-With, token");
                break;
            }
        }

        String token = request.getHeader("Authorization");

        if (StringUtils.hasText(token)) {

            try {
                if (jwtUtil.validateToken(token)) {
                    String userType = jwtUtil.getUserTypeFromToken(token);
                    String identifier = jwtUtil.getIdentifierFromToken(token);

                    // 根据用户类型构建不同的Redis key
                    String redisKey;
                    if ("business".equals(userType)) {
                        redisKey = "login:business:" + identifier;
                    } else {
                        redisKey = "login:customer:" + identifier;
                    }

                    LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(redisKey);

                    if (loginUser != null) {
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(loginUser, null, null);
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }
            } catch (Exception e) {
                log.error("JWT处理异常: {}", e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}