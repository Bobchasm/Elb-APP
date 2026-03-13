package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.BusinessInfoDTO;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.pojo.vo.UserVO;

import java.util.List;

public interface UserService {
    User getUserWithAuthorities(String username);

    void addUser(User user);

    void updateUser(User user);

    boolean isEmptyUserTable();

    User findByUsername(String username);

    UserVO changeUserStatus(String username);

    void deleteUser(String username);

    void toggleUserActivated(String username, Boolean activated);

    List<BusinessInfoDTO> getAllActiveBusinesses();
}