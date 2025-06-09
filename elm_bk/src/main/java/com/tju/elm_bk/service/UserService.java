package com.tju.elm_bk.service;

import com.tju.elm_bk.untity.User;

public interface UserService {
    public User getUserByIdByPass(String userId, String password);
    public int getUserById(String userId);
    public int saveUser(User user);
}