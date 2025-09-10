package com.tju.elm_bk.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class JwtUtil {

    // JWT密钥
    @Value("${jwt.secret}")
    private String secret;

    // JWT过期时间
    @Value("${jwt.expiration}")
    private Long expiration;

    // JWT令牌前缀
    @Value("${jwt.token-header}")
    private String tokenHeader;

    // 生成密钥
    private SecretKey getSecretKey() {
        try {
            // 验证密钥长度
            if (secret == null || secret.length() < 32) {
                log.error("JWT密钥长度不足，需要至少32字符，当前长度: {}",
                        secret != null ? secret.length() : "null");
                throw new IllegalArgumentException("JWT密钥长度不足");
            }

            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("生成JWT密钥失败: {}", e.getMessage());
            throw new RuntimeException("JWT密钥配置错误", e);
        }
    }

    /**
     * 生成JWT令牌
     * @param userId 用户ID
     * @return JWT令牌
     */
    public String createJWT(String userId) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);

        return Jwts.builder()
                .setClaims(claims) // 自定义载荷
                .setSubject(userId) // 主题
                .setIssuedAt(new Date()) // 签发时间
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 过期时间
                .signWith(getSecretKey(), SignatureAlgorithm.HS256) // 签名算法和密钥
                .compact();
    }

    /**
     * 解析JWT令牌
     * @param token JWT令牌
     * @return 用户ID
     */
    public String parseJWT(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // 返回用户ID
        } catch (ExpiredJwtException e) {
            log.error("JWT令牌已过期: {}", e.getMessage());
            throw new RuntimeException("JWT令牌已过期");
        } catch (MalformedJwtException e) {
            log.error("JWT令牌格式错误: {}", e.getMessage());
            throw new RuntimeException("JWT令牌格式错误");
        } catch (SignatureException e) {
            log.error("JWT签名验证失败: {}", e.getMessage());
            log.error("当前使用的密钥: {}", secret);
            throw new RuntimeException("JWT签名验证失败");
        } catch (Exception e) {
            log.error("JWT令牌解析失败: {}", e.getMessage());
            log.error("异常类型: {}", e.getClass().getName());
            throw new RuntimeException("JWT令牌解析失败: " + e.getMessage());
        }
    }

    /**
     * 验证JWT令牌是否有效
     * @param token JWT令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取JWT中的用户ID
     * @param token JWT令牌
     * @return 用户ID
     */
    public String getUserIdFromToken(String token) {
        return parseJWT(token);
    }

    /**
     * 获取JWT的过期时间
     * @param token JWT令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();
    }

    /**
     * 判断JWT是否过期
     * @param token JWT令牌
     * @return 是否过期
     */
    public boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}