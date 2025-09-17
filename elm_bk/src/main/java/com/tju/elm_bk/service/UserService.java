package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.result.Result;

import java.util.List;
import java.util.Optional;

public interface UserService {
    public Optional<User> getUserWithAuthorities();

    public User addUser(User user);

    public User updateUser(User user);

    public boolean isEmptyUserTable();

    public Optional<User> findByUsername(String username);
}