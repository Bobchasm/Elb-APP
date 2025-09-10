
// UserServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public User getUserByIdByPass(User user) {
        if(null!=user)
            return userMapper.getUserByIdByPass(user.getUserId(), user.getPassword());
        return null;
    }

    @Override
    public int getUserById(User user) {
        return userMapper.checkUserIdExists(user.getUserId());
    }

    @Override
    public Result saveUser(User user) {
        if(userMapper.checkUserIdExists(user.getUserId())!=0){
            return Result.fail("用户已存在");
        }
        // 加密密码
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        userMapper.saveUser(user);
        return Result.success("注册成功");
    }

    @Override
    public Result login(User user) {
        try {
            // 认证
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(user.getUserId(), user.getPassword());

            Authentication authenticate = authenticationManager.authenticate(authenticationToken);

            if (authenticate.isAuthenticated()) {
                // 认证成功，生成JWT
                LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
                String userId = loginUser.getUser().getUserId().toString();
                String jwt = jwtUtil.createJWT(userId);

                // 将用户信息存入Redis
                String redisKey = "login:user:" + userId;
                redisTemplate.opsForValue().set(redisKey, loginUser, 7, TimeUnit.DAYS);

                return Result.success(jwt);
            }

            return Result.fail("认证失败");
        } catch (AuthenticationException e) {
            return Result.fail("用户名或密码错误");
        }
    }

    @Override
    public Result logout() {
        //获取SecurityContextHolder中的用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUser().getUserId();
        //删除redis中的用户信息
        String redisKey = "login:user:" + userId;
        Boolean deleteResult = redisTemplate.delete(redisKey);

        if (Boolean.TRUE.equals(deleteResult)) {
            return new Result(0, "退出成功");
        } else {
            return Result.fail("退出登录失败，未找到用户缓存");
        }
    }
}
