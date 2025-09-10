package com.tju.elm_bk.service.impl;


import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;
    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {

        User user = userMapper.getUserById(userId);

        //如果没有该用户就抛出异常
        if (Objects.isNull(user)) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 将用户信息封装到UserDetails实现类中
        return new LoginUser(user);
    }
}
