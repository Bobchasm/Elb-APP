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
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 获取token
        String token = request.getHeader("Authorization");
        log.debug("收到Authorization头: {}", token);
        if (StringUtils.hasText(token)) {
            try {
                // 解析token
                String userId = jwtUtil.parseJWT(token);
                log.debug("解析出的userId: {}", userId);

                // 从Redis中获取用户信息
                String redisKey = "login:user:" + userId;
                LoginUser loginUser = (LoginUser) redisTemplate.opsForValue().get(redisKey);
                log.debug("Redis key: {}, 用户信息: {}", redisKey, loginUser);

                if (loginUser != null) {
                    // 存入SecurityContextHolder
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    log.info("用户认证成功: {}", userId);
                }else{
                    log.warn("Redis中未找到用户信息，userId: {}", userId);
                }
            } catch (Exception e) {
                log.error("JWT解析异常: {}", e.getMessage());
                log.error("请求URL: {}", request.getRequestURL());
                // 不要抛出异常，继续过滤器链
            }
        }

        filterChain.doFilter(request, response);
    }
}
