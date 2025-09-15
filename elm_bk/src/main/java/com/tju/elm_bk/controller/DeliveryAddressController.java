package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.impl.DeliveryAddressServiceImpl;
import com.tju.elm_bk.entity.DeliveryAddress;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "地址接口")
@RestController
@RequestMapping("/api/addresses")
public class DeliveryAddressController {
    @Autowired
    private DeliveryAddressServiceImpl deliveryAddressServiceImpl;

    @Operation(summary = "获取用户地址列表")
    @PostMapping("/listDeliveryAddressByUserId")
    public List<DeliveryAddress> listDeliveryAddressByUserId(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressServiceImpl.listDeliveryAddressByUserId(deliveryAddress);
    }

    @Operation(summary = "根据配送地址id获取地址")
    @PostMapping("/getDeliveryAddressById")
    public DeliveryAddress getDeliveryAddressById(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressServiceImpl.getDeliveryAddressById(deliveryAddress);
    }

    @Operation(summary = "保存配送地址")
    @PostMapping("/saveDeliveryAddress")
    public int saveDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        String contactName = deliveryAddress.getContactName();
        Integer contactSex = deliveryAddress.getContactSex();
        String contactTel = deliveryAddress.getContactTel();
        String address = deliveryAddress.getAddress();
        String userId = deliveryAddress.getUserId();

        return deliveryAddressServiceImpl.addDeliveryAddress(contactName,contactSex,contactTel,address,userId);
    }

    @Operation(summary = "更新配送地址")
    @PostMapping("/updateDeliveryAddress")
    public int updateDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressServiceImpl.updateDeliveryAddress(deliveryAddress);
    }

    @Operation(summary = "删除配送地址")
    @PostMapping("/removeDeliveryAddress")
    public int removeDeliveryAddress(@RequestBody DeliveryAddress deliveryAddress)
    {
        return deliveryAddressServiceImpl.deleteDeliveryAddress(deliveryAddress.getDaId());
    }

}
