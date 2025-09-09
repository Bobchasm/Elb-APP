package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.User;

public interface UserService {
    public User getUserByIdByPass(User user);
    public int getUserById(User user);
    public int saveUser(User user);
}