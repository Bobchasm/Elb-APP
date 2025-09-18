// OrdersMapper.java
package com.tju.elm_bk.mapper;
import com.tju.elm_bk.dto.OrderDTO;
import com.tju.elm_bk.entity.Order;

import com.tju.elm_bk.vo.OrderVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrdersMapper {
    List<OrderVO> selectOrders(Long userId);

    OrderVO selectOrderById(Long orderId);

    void insertOrder(Order order);
}