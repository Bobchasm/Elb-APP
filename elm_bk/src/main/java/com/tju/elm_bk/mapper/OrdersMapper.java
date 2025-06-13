// OrdersMapper.java
package com.tju.elm_bk.mapper;
import com.tju.elm_bk.untity.Orders;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {
//     @Insert("insert into orders(userId, businessId, orderDate, orderTotal, daId, orderState) " +
//             "values(#{userId}, #{businessId}, #{orderDate}, #{orderTotal}, #{daId}, 0)")
        @Insert("insert into orders (userId,businessId,orderDate,orderTotal,daId,orderState,deliveryPrice) values(#{userId},#{businessId},#{orderDate},#{orderTotal},#{daId},0,#{deliveryPrice})")
        @Options(useGeneratedKeys=true,keyProperty="orderId",keyColumn="orderId")    
        int saveOrders(Orders orders);

    @Select("select o.*, b.businessId as bbusinessId, b.businessName as bbusinessName, " +
            "b.deliveryPrice as bdeliveryPrice from elm.orders o " +
            "left join elm.business b on o.businessId = b.businessId where o.orderId = #{orderId}")
    Orders getOrdersById(Integer orderId);

    @Select("select o.*, b.businessId as bbusinessId, b.businessName as bbusinessName, " +
            "o.deliveryPrice as bdeliveryPrice from elm.orders o " +
            "left join elm.business b on o.businessId = b.businessId where o.userId = #{userId}")
    List<Orders> listOrdersByUserId(String userId);

        @Update("update orders set orderState = 1 where orderId = #{orderId}")
        int completeOrder(Integer orderId);

        @Select("select odId from orderdetailet where orderId=#{orderId}")
        public List<Integer> listOdIdByOrderId(Integer orderId);
}