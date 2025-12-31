package com.tju.elm.point.mapper;

import com.tju.elm.point.zoo.pojo.entity.PointsLotteryRecord;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointsLotteryRecordMapper {
    
    /**
     * 插入抽奖记录
     */
    @Insert("INSERT INTO points_lottery_record (user_id, member_level, lottery_type, points_reward, " +
            "points_multiplier, original_points, lottery_month, transaction_id, create_time, creator, " +
            "updater, is_deleted, update_time) " +
            "VALUES (#{userId}, #{memberLevel}, #{lotteryType}, #{pointsReward}, #{pointsMultiplier}, " +
            "#{originalPoints}, #{lotteryMonth}, #{transactionId}, #{createTime}, #{creator}, " +
            "#{updater}, #{isDeleted}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsLotteryRecord record);
    
    /**
     * 统计用户指定月份的抽奖次数
     */
    @Select("SELECT COUNT(*) FROM points_lottery_record " +
            "WHERE user_id = #{userId} AND lottery_month = #{lotteryMonth} AND is_deleted = 0")
    Integer countByUserIdAndMonth(Long userId, String lotteryMonth);
    
    /**
     * 查询用户指定月份的抽奖记录
     */
    @Select("SELECT * FROM points_lottery_record " +
            "WHERE user_id = #{userId} AND lottery_month = #{lotteryMonth} AND is_deleted = 0 " +
            "ORDER BY create_time DESC")
    List<PointsLotteryRecord> selectByUserIdAndMonth(Long userId, String lotteryMonth);
    
    /**
     * 查询用户最近的抽奖记录
     */
    @Select("SELECT * FROM points_lottery_record " +
            "WHERE user_id = #{userId} AND is_deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit}")
    List<PointsLotteryRecord> selectRecentByUserId(Long userId, Integer limit);
    
    /**
     * 根据ID查询抽奖记录
     */
    @Select("SELECT * FROM points_lottery_record WHERE id = #{id} AND is_deleted = 0")
    PointsLotteryRecord selectById(Long id);
    
    /**
     * 更新抽奖记录
     */
    @Update("UPDATE points_lottery_record SET transaction_id = #{transactionId}, " +
            "update_time = #{updateTime}, updater = #{updater} WHERE id = #{id}")
    void updateTransactionId(Long id, Long transactionId, LocalDateTime updateTime, Long updater);
}

