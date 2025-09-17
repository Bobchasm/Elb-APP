package com.tju.elm_bk.controller;

import com.tju.elm_bk.dto.AddressCreateDTO;
import com.tju.elm_bk.entity.DeliveryAddress;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.service.AddressService;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.vo.AddressVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@Tag(name="管理地址", description = "对配送地址的增删改查")
public class AddressController {
    @Autowired
    private AddressService addressService;

    @PostMapping("/addresses")
    @Operation(summary = "新增地址", description = "创建一个新的地址")
    public HttpResult<AddressVO> addDeliveryAddress(@Valid @RequestBody AddressCreateDTO createDTO) {
        return addressService.addDeliveryAddress(createDTO);
    }
}
