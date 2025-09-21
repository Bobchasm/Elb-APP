package com.tju.elm_bk.service;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.vo.BusinessVO;

import java.util.List;

public interface BusinessService {
    BusinessVO getBusinessById(Long id);
    BusinessVO updateBusiness(Long id, BusinessUpdateDTO updateDto);
    BusinessVO deleteBusiness(Long id);
    BusinessVO patchBusiness(Long id, BusinessUpdateDTO updateDto);
    List<BusinessVO> getBusinesses();
    BusinessVO addBusiness(BusinessDTO businessDto);
}
