// OrdersMapper.java
package com.tju.elm_bk.mapper;
import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.entity.Order;

import com.tju.elm_bk.vo.OrderItemDetailVO;
import com.tju.elm_bk.vo.OrderItemVO;
import com.tju.elm_bk.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {
    List<OrderVO> selectOrders(Long userId);

    OrderVO selectOrderById(Long orderId);

    void insertOrder(Order order);

    @Select("""
        <script>
            select o.id,o.order_total,o.order_state,o.order_date,o.business_id,b.business_name
            from orders o
            left join business b on b.id = o.business_id and b.is_deleted = 0
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
        </script>
    """)
    List<OrderItemVO> selectOrderItemsList(Long businessId, Integer orderState,Long userId);

    @Select("""
        <script>
            select o.*, uc.username as customerName, b.business_name, da.address,da.contact_name,da.contact_sex,da.contact_tel
            from orders o
            left join users uc on uc.id = o.customer_id and uc.is_deleted = 0
            left join business b on b.id = o.business_id and b.is_deleted = 0
            left join delivery_address da on da.id = o.address_id and da.is_deleted = 0
            where o.is_deleted = 0 and o.id = #{orderItemId}
        </script>
    """)
    OrderItemDetailVO selectOrderItemById(Long orderItemId);

    @Update("update orders set order_state = #{orderState} where id = #{orderId}")
    Integer setOrderState(Long orderId, Integer orderState);

    @Select("select * from orders where id = #{orderId}")
    Order getOrderById(Long orderId);


}