package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsExpiration;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointsExpirationMapper {
    
    /**
     * 插入积分过期记录
     */
    @Insert("INSERT INTO points_expiration (user_id, transaction_id, points_amount, expire_time, " +
            "expire_date, is_expired, create_time) " +
            "VALUES (#{userId}, #{transactionId}, #{pointsAmount}, #{expireTime}, #{expireDate}, " +
            "#{isExpired}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsExpiration expiration);
    
    /**
     * 查询用户即将过期的积分（按过期时间升序）
     */
    @Select("SELECT * FROM points_expiration WHERE user_id = #{userId} AND is_expired = 0 " +
            "AND expire_time > NOW() ORDER BY expire_time ASC")
    List<PointsExpiration> selectExpiringPoints(Long userId);
    
    /**
     * 根据过期日期查询即将过期的积分
     */
    @Select("SELECT user_id, SUM(points_amount) as points_amount, expire_date " +
            "FROM points_expiration WHERE expire_date = #{expireDate} AND is_expired = 0 " +
            "GROUP BY user_id, expire_date")
    List<PointsExpiration> selectByExpireDate(LocalDate expireDate);
    
    /**
     * 更新积分过期记录（扣减积分）
     */
    @Update("UPDATE points_expiration SET points_amount = #{pointsAmount}, is_expired = #{isExpired} " +
            "WHERE id = #{id}")
    void updateById(PointsExpiration expiration);
    
    /**
     * 查询已过期的积分
     */
    @Select("SELECT * FROM points_expiration WHERE expire_time < NOW() AND is_expired = 0")
    List<PointsExpiration> selectExpiredPoints();
    
    /**
     * 标记为已过期
     */
    @Update("UPDATE points_expiration SET is_expired = 1 WHERE id = #{id}")
    void markAsExpired(Long id);
    
    /**
     * 根据交易ID删除过期记录（逻辑删除，标记为已过期）
     */
    @Update("UPDATE points_expiration SET is_expired = 1 WHERE transaction_id = #{transactionId}")
    void deleteByTransactionId(@Param("transactionId") Long transactionId);
}

