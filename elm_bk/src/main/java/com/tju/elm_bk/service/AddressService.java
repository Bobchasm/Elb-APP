package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.AddressCreateDTO;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.vo.AddressVO;
import jakarta.validation.Valid;

public interface AddressService {
    HttpResult<AddressVO> addDeliveryAddress(@Valid AddressCreateDTO createDTO);
}
