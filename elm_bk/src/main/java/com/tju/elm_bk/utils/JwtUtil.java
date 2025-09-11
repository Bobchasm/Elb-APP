package com.tju.elm_bk.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
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

    // 生成密钥
    private SecretKey getSecretKey() {
        try {
            if (secret == null || secret.length() < 32) {
                throw new IllegalArgumentException("JWT密钥长度不足");
            }
            return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("JWT密钥配置错误", e);
        }
    }

    /**
     * 生成JWT令牌（包含用户类型信息）
     */
    public String createJWT(String userType, String identifier) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userType", userType);
        claims.put("identifier", identifier);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析JWT令牌并返回Claims
     */
    public Claims parseJWTClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("JWT令牌解析失败");
        }
    }

    /**
     * 从token中获取用户类型
     */
    public String getUserTypeFromToken(String token) {
        Claims claims = parseJWTClaims(token);
        return claims.get("userType", String.class);
    }

    /**
     * 从token中获取用户标识符
     */
    public String getIdentifierFromToken(String token) {
        Claims claims = parseJWTClaims(token);
        return claims.get("identifier", String.class);
    }

    /**
     * 验证JWT令牌是否有效
     */
    public boolean validateToken(String token) {
        try {
            parseJWTClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}