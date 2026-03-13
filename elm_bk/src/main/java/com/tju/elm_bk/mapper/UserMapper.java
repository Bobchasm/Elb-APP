// UserMapper.xml.java
package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.dto.BusinessInfoDTO;
import com.tju.elm_bk.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {
    @Select("SELECT * FROM users WHERE id = #{id} AND is_deleted = 0")
    User findById(Long id);
    @Select("SELECT * FROM users WHERE username = #{username} AND is_deleted = 0")
    User findByUsername(String username);
    User findByUsernameWithAuthorities(String username);
    User findByUserIdWithAuthorities(Long userId);
    void insert(User user);
    void update(User user);
    @Select("SELECT COUNT(*) FROM users WHERE is_deleted = 0")
    int count();
    @Insert("INSERT INTO user_authority (user_id, authority_name) VALUES (#{userId}, #{authorityName})")
    void insertUserAuthority(@Param("userId") Long userId, @Param("authorityName") String authorityName);

    @Select("select id from users where username = #{username}")
    Long getUserIdByUsername(String username);

    @Select("SELECT * FROM users WHERE id = #{userId} AND is_deleted = 0")
    Integer countUserById(@Param("userId") Long userId);

    @Update("UPDATE users SET activated = #{activated} WHERE id = #{id}")
    void updateActivated(User user);

    /**
     * 获取所有已激活的商家信息
     * @return 商家信息列表
     */
    @Select("SELECT " +
            "    p.id AS userId, " +  // 注意这里改为userId，与DTO字段名一致
            "    u.username, " +
            "    p.phone, " +
            "    p.photo " +
            "FROM " +
            "    user_authority ua " +
            "    JOIN users u ON ua.user_id = u.id " +
            "    JOIN person p ON ua.user_id = p.id " +
            "WHERE " +
            "    ua.authority_name = 'BUSINESS' " +
            "    AND u.is_deleted = 0 ")
    List<BusinessInfoDTO>getAllActiveBusinesses();
}