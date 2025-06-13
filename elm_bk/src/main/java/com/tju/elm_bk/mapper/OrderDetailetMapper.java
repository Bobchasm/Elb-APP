// OrderDetailetMapper.java
package com.tju.elm_bk.mapper;

import com.tju.elm_bk.untity.OrderDetailet;
import com.tju.elm_bk.untity.Orders;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailetMapper {
    @Insert("<script>" +
            "insert into elm.orderdetailet (orderId, foodId, quantity, foodName, foodPrice) values " +
            "<foreach collection='list' item='item' separator=','>" +
            "(#{item.orderId}, #{item.foodId}, #{item.quantity}, #{item.foodName}, #{item.foodPrice})" +
            "</foreach>" +
            "</script>")
    int saveOrderDetailetBatch(@Param("list") List<OrderDetailet> list);

    //通过orderId查询订单详情
    @Select("select * from elm.orderdetailet where orderId = #{orderId}")
    List<OrderDetailet> listOrderDetailetByOrderId(Integer orderId);

    @Select("select * from elm.orderdetailet where orderId=#{orderId}")
        List<OrderDetailet> listorderDetailetByOrderId(Orders orders);
}