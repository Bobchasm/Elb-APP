package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsTransaction;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PointsTransactionMapper {
    
    /**
     * 插入积分明细
     */
    @Insert("INSERT INTO points_transaction (user_id, account_id, transaction_type, points_source, " +
            "points_change, points_balance, expire_time, related_order_id, related_food_id, related_rule_id, " +
            "description, create_time, creator, update_time, updater, is_deleted) " +
            "VALUES (#{userId}, #{accountId}, #{transactionType}, #{pointsSource}, #{pointsChange}, " +
            "#{pointsBalance}, #{expireTime}, #{relatedOrderId}, #{relatedFoodId}, #{relatedRuleId}, " +
            "#{description}, #{createTime}, #{creator}, #{updateTime}, #{updater}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsTransaction transaction);
    
    /**
     * 根据ID查询积分明细
     */
    @Select("SELECT * FROM points_transaction WHERE id = #{id} AND is_deleted = 0")
    PointsTransaction selectById(Long id);
    
    /**
     * 根据用户ID查询积分明细列表
     */
    List<PointsTransaction> selectByUserId(@Param("userId") Long userId, 
                                           @Param("transactionType") Integer transactionType,
                                           @Param("pointsSource") Integer pointsSource,
                                           @Param("offset") Integer offset, 
                                           @Param("limit") Integer limit);
    
    /**
     * 根据用户ID统计积分明细总数
     */
    @Select("SELECT COUNT(*) FROM points_transaction WHERE user_id = #{userId} AND is_deleted = 0 " +
            "AND (#{transactionType} IS NULL OR transaction_type = #{transactionType})")
    Long countByUserId(@Param("userId") Long userId, @Param("transactionType") Integer transactionType);
    
    /**
     * 根据订单ID和积分来源查询积分明细（用于幂等性检查）
     */
    @Select("SELECT * FROM points_transaction WHERE related_order_id = #{orderId} " +
            "AND points_source = #{pointsSource} AND is_deleted = 0 LIMIT 1")
    PointsTransaction selectByOrderIdAndSource(@Param("orderId") Long orderId, 
                                               @Param("pointsSource") Integer pointsSource);
    
    /**
     * 根据账户ID查询积分明细列表
     */
    @Select("SELECT * FROM points_transaction WHERE account_id = #{accountId} AND is_deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<PointsTransaction> selectByAccountId(@Param("accountId") Long accountId,
                                              @Param("offset") Integer offset,
                                              @Param("limit") Integer limit);
    
    /**
     * 根据订单ID和交易类型查询积分明细（用于解冻积分）
     */
    @Select("SELECT * FROM points_transaction WHERE related_order_id = #{orderId} " +
            "AND transaction_type = #{transactionType} AND is_deleted = 0")
    List<PointsTransaction> selectByOrderIdAndType(@Param("orderId") Long orderId,
                                                   @Param("transactionType") Integer transactionType);
    
    /**
     * 根据订单ID和积分来源查询所有积分明细（用于解冻/取消奖励积分）
     */
    @Select("SELECT * FROM points_transaction WHERE related_order_id = #{orderId} " +
            "AND points_source = #{pointsSource} AND is_deleted = 0")
    List<PointsTransaction> selectByOrderIdAndSourceList(@Param("orderId") Long orderId, 
                                                         @Param("pointsSource") Integer pointsSource);
    
    /**
     * 根据ID删除积分明细（逻辑删除）
     */
    @Update("UPDATE points_transaction SET is_deleted = 1, update_time = #{updateTime}, " +
            "updater = #{updater} WHERE id = #{id}")
    void deleteById(@Param("id") Long id, 
                   @Param("updateTime") java.time.LocalDateTime updateTime,
                   @Param("updater") Long updater);
}

