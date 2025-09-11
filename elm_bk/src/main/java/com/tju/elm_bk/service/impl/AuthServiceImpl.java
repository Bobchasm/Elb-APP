package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.entity.LoginRequest;
import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.AuthService;
import com.tju.elm_bk.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Result login(LoginRequest loginRequest, String userType) {
        try {
            // 验证用户类型
            if (!"customer".equals(userType) && !"business".equals(userType)) {
                return Result.fail("无效的用户类型");
            }

            // 加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsernameAndType(
                    loginRequest.getPhoneNumber(), userType);

            // 验证密码
            if (!passwordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
                return Result.fail("密码错误");
            }

            LoginUser loginUser = (LoginUser) userDetails;
            String identifier = loginUser.getUsername();//实际为手机号码
            String jwt = jwtUtil.createJWT(userType, identifier);

            // 存储到Redis
            String redisKey = "login:" + userType + ":" + identifier;
            redisTemplate.opsForValue().set(redisKey, loginUser, 7, TimeUnit.DAYS);

            return Result.success(jwt);

        } catch (UsernameNotFoundException e) {
            return Result.fail("用户不存在");
        } catch (Exception e) {
            return Result.fail("登录失败: " + e.getMessage());
        }
    }
}
