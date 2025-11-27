package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsExpirationAlertLog;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointsExpirationAlertLogMapper {
    
    /**
     * 插入预警记录
     */
    @Insert("INSERT INTO points_expiration_alert_log (user_id, points_amount, expire_date, " +
            "alert_time, next_alert_time, is_sent, phone, create_time) " +
            "VALUES (#{userId}, #{pointsAmount}, #{expireDate}, #{alertTime}, " +
            "#{nextAlertTime, jdbcType=TIMESTAMP}, #{isSent}, #{phone}, #{createTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsExpirationAlertLog log);
    
    /**
     * 根据用户ID和过期日期查询预警记录
     */
    @Select("SELECT * FROM points_expiration_alert_log WHERE user_id = #{userId} " +
            "AND expire_date = #{expireDate} ORDER BY id DESC LIMIT 1")
    PointsExpirationAlertLog selectByUserIdAndExpireDate(@Param("userId") Long userId,
                                                          @Param("expireDate") LocalDate expireDate);
    
    /**
     * 更新预警记录
     */
    @Update("UPDATE points_expiration_alert_log SET alert_time = #{alertTime}, " +
            "next_alert_time = #{nextAlertTime, jdbcType=TIMESTAMP}, is_sent = #{isSent} WHERE id = #{id}")
    void updateById(PointsExpirationAlertLog log);
    
    /**
     * 查询需要发送的预警（next_alert_time <= 当前时间）
     */
    @Select("SELECT * FROM points_expiration_alert_log WHERE next_alert_time <= #{now} " +
            "AND is_sent = 0 ORDER BY next_alert_time ASC")
    List<PointsExpirationAlertLog> selectPendingAlerts(LocalDateTime now);
}

