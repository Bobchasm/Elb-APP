package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.LoginRequest;
import com.tju.elm_bk.result.Result;

public interface AuthService {
    Result login(LoginRequest loginRequest, String userType);
}
