package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.DeliveryAddressService;
import com.tju.elm_bk.untity.DeliveryAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/DeliveryAddressController")
public class DeliveryAddressController {
    @Autowired
    private DeliveryAddressService deliveryAddressService;

    @GetMapping("/listDeliveryAddressByUserId")
    public List<DeliveryAddress> listDeliveryAddressByUserId(String userId)
    {
        return deliveryAddressService.listDeliveryAddressByUserId(userId);
    }

    @GetMapping("/getDeliveryAddressById")
    public DeliveryAddress getDeliveryAddressById(int daId)
    {
        return deliveryAddressService.getDeliveryAddressById(daId);
    }

    @PostMapping("/saveDeliveryAddress")
    public int saveDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        String contactName = deliveryAddress.getContactName();
        Integer contactSex = deliveryAddress.getContactSex();
        String contactTel = deliveryAddress.getContactTel();
        String address = deliveryAddress.getAddress();
        String userId = deliveryAddress.getUserId();

        return deliveryAddressService.addDeliveryAddress(contactName,contactSex,contactTel,address,userId);
    }

    @PostMapping("/updateDeliveryAddress")
    public int updateDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressService.updateDeliveryAddress(deliveryAddress);
    }

    @PostMapping("/removeDeliveryAddress")
    public int removeDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressService.deleteDeliveryAddress(deliveryAddress.getDaId());
    }

}
