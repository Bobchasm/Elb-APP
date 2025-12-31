package com.tju.elm.user.mapper;

import com.tju.elm.user.zoo.pojo.entity.DeliveryAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {

    @Select("SELECT * FROM delivery_address WHERE id=#{id}")
    public DeliveryAddress getDeliveryAddressById(Long id);

    public int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    void insert(DeliveryAddress address);

    @Select("SELECT * FROM delivery_address WHERE user_id=#{userId} AND is_deleted=0")
    List<DeliveryAddress> listDeliveryAddressByUserId(Long userId);
}
