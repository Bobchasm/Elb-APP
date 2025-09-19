// OrderDetailetMapper.java
package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.OrderDetailet;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailetMapper {
    @Insert("""
        insert into orderdetailet (create_time, creator, is_deleted, update_time, updater, quantity, food_id, order_id)
        values (#{createTime}, #{creator}, #{isDeleted}, #{updateTime}, #{updater}, #{quantity}, #{foodId}, #{orderId})
    """)
    Integer saveOrderDetail(OrderDetailet orderDetailet);


}