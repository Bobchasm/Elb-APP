package com.tju.elm_bk.mapper;

import com.tju.elm_bk.pojo.entity.PointsExchangeOrder;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PointsExchangeOrderMapper {
    
    /**
     * 插入积分兑换订单
     */
    @Insert("INSERT INTO points_exchange_order (user_id, order_id, food_id, " +
            "points_used, cash_amount, exchange_ratio, status, create_time, update_time, is_deleted) " +
            "VALUES (#{userId}, #{orderId}, #{foodId}, #{pointsUsed}, " +
            "#{cashAmount}, #{exchangeRatio}, #{status}, #{createTime}, #{updateTime}, #{isDeleted})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(PointsExchangeOrder order);
    
    /**
     * 根据ID查询积分兑换订单
     */
    @Select("SELECT * FROM points_exchange_order WHERE id = #{id} AND is_deleted = 0")
    PointsExchangeOrder selectById(Long id);
    
    /**
     * 根据用户ID查询积分兑换订单列表
     */
    @Select("SELECT * FROM points_exchange_order WHERE user_id = #{userId} AND is_deleted = 0 " +
            "ORDER BY create_time DESC LIMIT #{limit} OFFSET #{offset}")
    List<PointsExchangeOrder> selectByUserId(@Param("userId") Long userId,
                                             @Param("offset") Integer offset,
                                             @Param("limit") Integer limit);
    
    /**
     * 根据订单ID查询积分兑换订单
     */
    @Select("SELECT * FROM points_exchange_order WHERE order_id = #{orderId} AND is_deleted = 0 LIMIT 1")
    PointsExchangeOrder selectByOrderId(Long orderId);
    
    /**
     * 更新积分兑换订单状态
     */
    @Update("UPDATE points_exchange_order SET status = #{status}, update_time = #{updateTime} " +
            "WHERE id = #{id}")
    void updateStatus(@Param("id") Long id, @Param("status") Integer status, 
                     @Param("updateTime") java.time.LocalDateTime updateTime);
}

