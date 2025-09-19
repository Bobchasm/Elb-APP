package com.tju.elm_bk.service;

import com.tju.elm_bk.entity.DeliveryAddress;

import java.util.List;

public interface DeliveryAddressService {

    List<DeliveryAddress> listDeliveryAddressByUserId(DeliveryAddress deliveryAddress);

    DeliveryAddress getDeliveryAddressById(DeliveryAddress deliveryAddress);

    int addDeliveryAddress(String contactName,int contactSex,String contactTel,String address,String userId);

    int updateDeliveryAddress(DeliveryAddress deliveryAddress);

    int deleteDeliveryAddress(int daId);
}
