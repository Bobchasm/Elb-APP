package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.DeliveryAddressMapper;
import com.tju.elm_bk.service.DeliveryAddressService;
import com.tju.elm_bk.entity.DeliveryAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeliveryAddressServiceImpl implements DeliveryAddressService {
    @Autowired
    DeliveryAddressMapper deliveryAddressMapper;

    @Override
    public List<DeliveryAddress> listDeliveryAddressByUserId(DeliveryAddress deliveryAddress)
    {
        if(null!=deliveryAddressMapper)
            return deliveryAddressMapper.listDeliveryAddress(deliveryAddress.getUserId());
        return null;
    }

    @Override
    public DeliveryAddress getDeliveryAddressById(DeliveryAddress deliveryAddress)
    {
        if(null!=deliveryAddressMapper)
            return deliveryAddressMapper.getDeliveryAddressById(deliveryAddress.getDaId());
        return null;
    }

    @Override
    public int addDeliveryAddress(String contactName,int contactSex,String contactTel,String address,String userId)
    {
        return deliveryAddressMapper.addDeliveryAddress(contactName,contactSex,contactTel,address,userId);
    }

    @Override
    public int updateDeliveryAddress(DeliveryAddress deliveryAddress)
    {
        return deliveryAddressMapper.updateDeliveryAddress(deliveryAddress);
    }

    @Override
    public int deleteDeliveryAddress(int daId)
    {
        return deliveryAddressMapper.deleteDeliveryAddress(daId);
    }
}
