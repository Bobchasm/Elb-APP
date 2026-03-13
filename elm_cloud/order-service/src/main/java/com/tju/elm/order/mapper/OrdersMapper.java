package com.tju.elm.order.mapper;

import com.tju.elm.order.zoo.pojo.vo.Order;
import com.tju.elm.order.zoo.pojo.vo.OrderItemDetailVO;
import com.tju.elm.order.zoo.pojo.vo.OrderItemVO;
import com.tju.elm.order.zoo.pojo.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {

    void insertOrderPlus(Order order);

    @Update("update orders set points_used = 1 where id = #{orderId}")
    void setOrderIsPoint(Long orderId);

    @Select("""
        <script>
            select o.id,o.order_total,o.order_state,o.order_date,o.business_id,o.delivery_price
            from orders o
            <where>
                o.is_deleted = 0
                <if test="null != businessId">
                    and o.business_id = #{businessId}
                </if>
                <if test="null != orderState">
                    and o.order_state = #{orderState}
                </if>
                <if test="null != userId">
                    and o.customer_id = #{userId}
                </if>
            </where>
              order by o.order_date desc
        </script>
    """)
    List<OrderItemVO> selectOrderItemsList(Long businessId, Integer orderState, Long userId);

    @Select("""
        <script>
            select o.*
            from orders o
            <where>
                o.is_deleted = 0
                <if test="null != businessId">
                    and o.business_id = #{businessId}
                </if>
                <if test="null != orderState">
                    and o.order_state = #{orderState}
                </if>
            </where>
              order by o.order_date desc
        </script>
    """)
    List<OrderItemDetailVO> selectOrderDetailetItem(Long businessId, Integer orderState);

    @Select("""
        <script>
            select o.*
            from orders o
            where o.is_deleted = 0 and o.id = #{orderItemId}
        </script>
    """)
    OrderItemDetailVO selectOrderItemById(Long orderItemId);



    @Update("update orders set order_state = #{orderState} where id = #{orderId}")
    Integer setOrderState(Long orderId, Integer orderState);

    @Update("update orders set payment_method = #{method} where id = #{orderId}")
    Integer setOrderPaymentMethod(Long orderId, Integer method);
    
    @Update("update orders set points_used = #{pointsUsed}, points_discount_amount = #{pointsDiscountAmount} where id = #{orderId}")
    Integer updateOrderPoints(@Param("orderId") Long orderId, 
                              @Param("pointsUsed") Long pointsUsed, 
                              @Param("pointsDiscountAmount") java.math.BigDecimal pointsDiscountAmount);
    
    @Update("update orders set points_amount = #{pointsAmount} where id = #{orderId}")
    Integer updateOrderPointsAmount(@Param("orderId") Long orderId, 
                                     @Param("pointsAmount") Long pointsAmount);

    @Select("select * from orders where id = #{orderId}")
    Order getOrderById(Long orderId);

    @Select("select sum(order_total) from orders where order_state = 3 and is_deleted = 0")
    Double countPrice();

    // AI服务相关查询方法
    @Select("SELECT * FROM orders WHERE id = #{id} AND is_deleted = 0")
    Order selectById(Long id);

    @Select("SELECT * FROM orders WHERE customer_id = #{userId} AND is_deleted = 0 " +
            "ORDER BY order_date DESC LIMIT #{limit}")
    List<Order> selectRecentOrdersByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    /**
     * 查询已支付且支付时间早于指定时间的订单（用于自动完成）
     * @param orderState 订单状态（1-已支付）
     * @param beforeTime 早于该时间的订单（通常是7天前）
     * @return 订单列表
     */
    @Select("SELECT * FROM orders WHERE order_state = #{orderState} AND is_deleted = 0 " +
            "AND order_date <= #{beforeTime}")
    List<Order> selectPaidOrdersBeforeTime(@Param("orderState") Integer orderState, 
                                           @Param("beforeTime") java.time.LocalDateTime beforeTime);

    @Select("SELECT COUNT(*) FROM `orders` WHERE business_id = #{businessId} AND order_state = 3 AND is_deleted = 0")
    Integer getSalesCount(@Param("businessId") Long businessId);

    OrderVO selectOrderById(Long orderId);
}