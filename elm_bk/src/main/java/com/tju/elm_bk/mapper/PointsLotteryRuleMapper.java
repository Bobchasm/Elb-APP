package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsLotteryRule;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface PointsLotteryRuleMapper {
    
    /**
     * 根据会员等级查询启用的抽奖规则（按排序和概率排序）
     */
    @Select("SELECT * FROM points_lottery_rule " +
            "WHERE member_level = #{memberLevel} AND rule_status = 1 AND is_deleted = 0 " +
            "AND (start_time IS NULL OR start_time <= NOW()) " +
            "AND (end_time IS NULL OR end_time >= NOW()) " +
            "ORDER BY prize_order ASC, probability DESC")
    List<PointsLotteryRule> selectByMemberLevel(Integer memberLevel);
    
    /**
     * 根据ID查询规则
     */
    @Select("SELECT * FROM points_lottery_rule WHERE id = #{id} AND is_deleted = 0")
    PointsLotteryRule selectById(Long id);
    
    /**
     * 插入抽奖规则
     */
    @Insert("INSERT INTO points_lottery_rule (rule_name, member_level, prize_type, prize_points, " +
            "prize_multiplier, probability, prize_order, prize_description, rule_status, " +
            "start_time, end_time, create_time, creator, updater, is_deleted) " +
            "VALUES (#{ruleName}, #{memberLevel}, #{prizeType}, #{prizePoints}, " +
            "#{prizeMultiplier}, #{probability}, #{prizeOrder}, #{prizeDescription}, #{ruleStatus}, " +
            "#{startTime}, #{endTime}, #{createTime}, #{creator}, #{updater}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsLotteryRule rule);
    
    /**
     * 更新抽奖规则
     */
    @Update("UPDATE points_lottery_rule SET " +
            "rule_name = #{ruleName}, member_level = #{memberLevel}, prize_type = #{prizeType}, " +
            "prize_points = #{prizePoints}, prize_multiplier = #{prizeMultiplier}, " +
            "probability = #{probability}, prize_order = #{prizeOrder}, " +
            "prize_description = #{prizeDescription}, rule_status = #{ruleStatus}, " +
            "start_time = #{startTime}, end_time = #{endTime}, " +
            "update_time = #{updateTime}, updater = #{updater} " +
            "WHERE id = #{id} AND is_deleted = 0")
    void updateById(PointsLotteryRule rule);
    
    /**
     * 逻辑删除抽奖规则
     */
    @Update("UPDATE points_lottery_rule SET is_deleted = 1, update_time = #{updateTime}, " +
            "updater = #{updater} WHERE id = #{id}")
    void deleteById(@Param("id") Long id, @Param("updateTime") LocalDateTime updateTime, 
                   @Param("updater") Long updater);
    
    /**
     * 查询抽奖规则列表（分页）
     */
    @Select("<script>" +
            "SELECT * FROM points_lottery_rule WHERE is_deleted = 0 " +
            "<if test='memberLevel != null'> AND member_level = #{memberLevel} </if>" +
            "<if test='ruleStatus != null'> AND rule_status = #{ruleStatus} </if>" +
            "ORDER BY member_level ASC, prize_order ASC, id DESC " +
            "LIMIT #{limit} OFFSET #{offset}" +
            "</script>")
    List<PointsLotteryRule> selectRules(@Param("memberLevel") Integer memberLevel,
                                       @Param("ruleStatus") Integer ruleStatus,
                                       @Param("offset") Integer offset,
                                       @Param("limit") Integer limit);
    
    /**
     * 统计抽奖规则总数
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM points_lottery_rule WHERE is_deleted = 0 " +
            "<if test='memberLevel != null'> AND member_level = #{memberLevel} </if>" +
            "<if test='ruleStatus != null'> AND rule_status = #{ruleStatus} </if>" +
            "</script>")
    Long countRules(@Param("memberLevel") Integer memberLevel, 
                   @Param("ruleStatus") Integer ruleStatus);
}

