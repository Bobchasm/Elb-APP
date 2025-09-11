package com.tju.elm_bk.service.impl;


import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserMapper userMapper;

    @Autowired
    BusinessMapper businessMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 这个方法不再使用，使用下面的方法
        throw new UnsupportedOperationException("请使用loadUserByUsernameAndType方法");
    }

    /**
     * 根据用户类型加载用户信息
     */
    public UserDetails loadUserByUsernameAndType(String username, String userType) throws UsernameNotFoundException {
        if ("customer".equals(userType)) {
            // 顾客登录 - 按手机号查询
            User user = userMapper.getUserById(username);
            if (user == null) {
                throw new UsernameNotFoundException("顾客不存在");
            }
            return new LoginUser(user, "customer");
        } else if ("business".equals(userType)) {
            // 商家登录 - 按手机号查询
            Business business = businessMapper.getBusinessByPhoneNumber(username);
            if (business == null) {
                throw new UsernameNotFoundException("商家不存在");
            }
            return new LoginUser(business, "business");
        } else {
            throw new UsernameNotFoundException("未知的用户类型");
        }
    }
}
