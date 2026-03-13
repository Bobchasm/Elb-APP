package com.tju.elm.order.mapper;

import com.tju.elm.order.zoo.pojo.vo.OrderDetailet;
import com.tju.elm.order.zoo.pojo.vo.OrderFoodVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailetMapper {

    Integer saveOrderDetail(OrderDetailet orderDetailet);




    Integer saveOrderDetailPlus(OrderDetailet orderDetailet);

    @Select("""
        select od.id,od.quantity,od.food_id,
           od.food_price,
           o.id as order_id
        from orderdetailet od
        left join orders o on o.id = od.order_id
        where od.order_id = #{orderId}
    """)
    List<OrderFoodVO> selectOrderDetailList(Long orderId);
}