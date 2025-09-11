package com.tju.elm_bk.entity;

import lombok.Data;

@Data
public class LoginRequest {
    private String phoneNumber;
    private String password;
}
