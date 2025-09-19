
// UserServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

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
    public int saveUser(User user) {
        return userMapper.saveUser(user);
    }
}
