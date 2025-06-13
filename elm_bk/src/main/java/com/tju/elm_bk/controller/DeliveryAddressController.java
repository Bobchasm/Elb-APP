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

    @PostMapping("/listDeliveryAddressByUserId")
    public List<DeliveryAddress> listDeliveryAddressByUserId(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressService.listDeliveryAddressByUserId(deliveryAddress);
    }

    @PostMapping("/getDeliveryAddressById")
    public DeliveryAddress getDeliveryAddressById(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressService.getDeliveryAddressById(deliveryAddress);
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
