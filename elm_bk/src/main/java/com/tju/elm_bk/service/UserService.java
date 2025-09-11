package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.result.Result;

public interface UserService {
    public User getUserByIdByPass(User user);
    public int getUserById(User user);
    public Result saveUser(User user);

    Result logout();
}