package com.tju.elm_bk.service;

import com.tju.elm_bk.mapper.DeliveryAddressMapper;
import com.tju.elm_bk.untity.DeliveryAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryAddressService {
    @Autowired
    DeliveryAddressMapper deliveryAddressMapper;

    public List<DeliveryAddress> listDeliveryAddressByUserId(String userId)
    {
        return deliveryAddressMapper.listDeliveryAddress(userId);
    }

    public DeliveryAddress getDeliveryAddressById(int daId)
    {
        return deliveryAddressMapper.getDeliveryAddressById(daId);
    }

    public int addDeliveryAddress(String contactName,int contactSex,String contactTel,String address,String userId)
    {
        return deliveryAddressMapper.addDeliveryAddress(contactName,contactSex,contactTel,address,userId);
    }

    public int updateDeliveryAddress(DeliveryAddress deliveryAddress)
    {
        return deliveryAddressMapper.updateDeliveryAddress(deliveryAddress);
    }

    public int deleteDeliveryAddress(int daId)
    {
        return deliveryAddressMapper.deleteDeliveryAddress(daId);
    }
}
