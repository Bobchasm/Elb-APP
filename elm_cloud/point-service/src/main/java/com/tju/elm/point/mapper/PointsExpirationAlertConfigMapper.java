package com.tju.elm.point.mapper;

import com.tju.elm.point.zoo.pojo.entity.PointsExpirationAlertConfig;
import org.apache.ibatis.annotations.*;

@Mapper
public interface PointsExpirationAlertConfigMapper {
    
    /**
     * 查询预警配置（通常只有一条记录）
     */
    @Select("SELECT * FROM points_expiration_alert_config ORDER BY id DESC LIMIT 1")
    PointsExpirationAlertConfig selectConfig();
    
    /**
     * 插入预警配置
     */
    @Insert("INSERT INTO points_expiration_alert_config (alert_days, alert_cycle, sms_template, " +
            "is_enabled, create_time, update_time) " +
            "VALUES (#{alertDays}, #{alertCycle}, #{smsTemplate}, #{isEnabled}, " +
            "#{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsExpirationAlertConfig config);
    
    /**
     * 更新预警配置
     */
    @Update("UPDATE points_expiration_alert_config SET alert_days = #{alertDays}, " +
            "alert_cycle = #{alertCycle}, sms_template = #{smsTemplate}, is_enabled = #{isEnabled}, " +
            "update_time = #{updateTime} WHERE id = #{id}")
    void updateById(PointsExpirationAlertConfig config);
}

