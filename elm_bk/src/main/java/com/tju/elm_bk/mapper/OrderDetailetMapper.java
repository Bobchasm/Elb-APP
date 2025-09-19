// OrderDetailetMapper.java
package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.OrderDetailet;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OrderDetailetMapper {

    Integer saveOrderDetail(OrderDetailet orderDetailet);


}