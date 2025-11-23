package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsAccount;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PointsAccountMapper {
    
    /**
     * 根据用户ID查询积分账户（带行锁）
     */
    @Select("SELECT * FROM points_account WHERE user_id = #{userId} AND is_deleted = 0 FOR UPDATE")
    PointsAccount selectForUpdate(Long userId);
    
    /**
     * 根据用户ID查询积分账户
     */
    @Select("SELECT * FROM points_account WHERE user_id = #{userId} AND is_deleted = 0")
    PointsAccount selectByUserId(Long userId);
    
    /**
     * 根据ID查询积分账户
     */
    @Select("SELECT * FROM points_account WHERE id = #{id} AND is_deleted = 0")
    PointsAccount selectById(Long id);
    
    /**
     * 插入积分账户
     */
    @Insert("INSERT INTO points_account (user_id, total_points, available_points, frozen_points, member_level, " +
            "create_time, creator, updater, is_deleted, update_time) " +
            "VALUES (#{userId}, #{totalPoints}, #{availablePoints}, #{frozenPoints}, #{memberLevel}, " +
            "#{createTime}, #{creator}, #{updater}, #{isDeleted}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsAccount pointsAccount);
    
    /**
     * 更新积分账户
     */
    @Update("UPDATE points_account SET total_points = #{totalPoints}, available_points = #{availablePoints}, " +
            "frozen_points = #{frozenPoints}, member_level = #{memberLevel}, update_time = #{updateTime}, " +
            "updater = #{updater} WHERE id = #{id}")
    void updateById(PointsAccount pointsAccount);
    
    /**
     * 更新积分余额
     */
    @Update("UPDATE points_account SET total_points = #{totalPoints}, available_points = #{availablePoints}, " +
            "update_time = #{updateTime}, updater = #{updater} WHERE id = #{id}")
    void updateBalance(PointsAccount pointsAccount);
}

