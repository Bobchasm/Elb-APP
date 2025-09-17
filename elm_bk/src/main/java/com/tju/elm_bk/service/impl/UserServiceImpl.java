
// UserServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;


    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities() {
        String username = String.valueOf(SecurityUtils.getCurrentUsername());
        return username != null ? Optional.ofNullable(userMapper.findByUsernameWithAuthorities(username)) : Optional.empty();
    }

    public User addUser(User user) {
        userMapper.insert(user);
        return user;
    }

    public User updateUser(User user) {
        userMapper.update(user);
        return user;
    }

    public boolean isEmptyUserTable() {
        return userMapper.count() == 0;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return Optional.ofNullable(userMapper.findByUsername(username));
    }
}
