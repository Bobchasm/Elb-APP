package com.tju.elm_bk.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginUser implements UserDetails {

    private Object userInfo; // 可以是User或Business对象
    private String userType; // 用户类型：customer 或 business
    private List<GrantedAuthority> authorities = Collections.emptyList(); // 初始化为空列表

    public LoginUser(Object userInfo,String userType){
        this.userInfo=userInfo;
        this.userType=userType;
    }

    // 添加setter方法
    public void setAuthorities(List<GrantedAuthority> authorities) {
        this.authorities = authorities != null ? authorities : Collections.emptyList();
    }

    // 获取权限 - 返回空列表
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    // 获取密码
    @Override
    public String getPassword() {
        if ("customer".equals(userType) && userInfo instanceof User) {
            return ((User) userInfo).getPassword();
        } else if ("business".equals(userType) && userInfo instanceof Business) {
            return ((Business) userInfo).getPassword();
        }
        return null;
    }

    // 获取用户名（手机号）
    @Override
    public String getUsername() {
        if ("customer".equals(userType) && userInfo instanceof User) {
            return ((User) userInfo).getUserId();
        } else if ("business".equals(userType) && userInfo instanceof Business) {
            return ((Business) userInfo).getPhoneNumber();
        }
        return null;
    }

    // 获取用户ID
//    public String getUserId() {
//        if ("customer".equals(userType) && userInfo instanceof User) {
//            return ((User) userInfo).getUserId();
//        } else if ("business".equals(userType) && userInfo instanceof Business) {
//            return ((Business) userInfo).getBusinessId();
//        }
//        return null;
//    }

    // 账户是否未过期
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 账户是否未锁定
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 密码是否未过期
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 账户是否可用
    @Override
    public boolean isEnabled() {
        return true;
    }
}