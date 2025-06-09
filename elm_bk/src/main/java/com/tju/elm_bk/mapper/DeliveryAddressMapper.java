package com.tju.elm_bk.mapper;

import com.tju.elm_bk.untity.DeliveryAddress;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DeliveryAddressMapper {
    @Select("SELECT * FROM deliveryaddress WHERE userId=#{userId}")
    public List<DeliveryAddress> listDeliveryAddress(String userId);

    @Select("SELECT * FROM deliveryaddress WHERE daId=#{daId}")
    public DeliveryAddress getDeliveryAddressById(int daId);

    @Insert("INSERT INTO deliveryaddress (contactName,contactSex,contactTel,address,userId) VALUES (#{contactName},#{contactSex},#{contactTel},#{address},#{userId})")
    public int addDeliveryAddress(String contactName,int contactSex,String contactTel,String address,String userId);

    @Update("UPDATE deliveryaddress SET contactName=#{contactName},contactSex=#{contactSex},contactTel=#{contactTel},address=#{address},userId=#{userId} WHERE daId=#{daId}")
    public int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    @Delete("DELETE FROM deliveryaddress WHERE daId=#{daId}")
    public int deleteDeliveryAddress(int daId);
}
