
// UserServiceImpl.java
package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.service.UserService;
import com.tju.elm_bk.untity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User getUserByIdByPass(String userId, String password) {
        return userMapper.getUserByIdByPass(userId, password);
    }

    @Override
    public int getUserById(String userId) {
        return userMapper.checkUserIdExists(userId);
    }

    @Override
    public int saveUser(User user) {
        return userMapper.saveUser(user);
    }
}
