// UserMapper.java
package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM elm.user WHERE userId = #{userId} AND password = #{password}")
    User getUserByIdByPass(String userId, String password);

    @Select("SELECT COUNT(3) FROM elm.user WHERE userId = #{userId}")
    int checkUserIdExists(String userId);

    @Insert("INSERT INTO elm.user(userId, password, userName, userSex) " +
            "VALUES(#{userId}, #{password}, #{userName}, #{userSex})")
    void saveUser(User user);

    @Select("SELECT * FROM elm.user WHERE userName = #{username}")
    User getUserByName(String username);

    @Select("SELECT * FROM elm.user WHERE userId = #{userId}")
    User getUserById(String userId);
}