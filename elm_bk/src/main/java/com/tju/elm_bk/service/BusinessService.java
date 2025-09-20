package com.tju.elm_bk.service;


import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;

import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.vo.BusinessSearchVO;
import com.tju.elm_bk.vo.BusinessVO;

import java.util.List;

public interface BusinessService {
    BusinessVO getBusinessById(Integer id);
    BusinessVO updateBusiness(Integer id, BusinessUpdateDTO updateDto);
    BusinessVO deleteBusiness(Integer id);
    BusinessVO patchBusiness(Integer id, BusinessUpdateDTO updateDto);
    List<BusinessVO> getBusinesses();
    BusinessVO addBusiness(BusinessDTO businessDto);
    List<BusinessSearchVO> getBusinessesBySearch(String keyword, boolean isScore,boolean isSales);
    Integer applyForAddBusiness(Business  business);
}
