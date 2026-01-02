package com.tju.elm.business.service;


import com.tju.elm.business.pojo.dto.BusinessDTO;
import com.tju.elm.business.pojo.dto.BusinessInfoDTO;
import com.tju.elm.business.pojo.dto.BusinessUpdateDTO;
import com.tju.elm.business.pojo.entity.Business;
import com.tju.elm.business.pojo.vo.BusinessSearchVO;
import com.tju.elm.business.pojo.vo.BusinessVO;
import com.tju.elm.business.pojo.vo.MerchantStatsVO;

import java.util.List;

public interface BusinessService {
    BusinessVO getBusinessById(Long id);
    BusinessVO updateBusiness(Long id, BusinessUpdateDTO updateDto);
    BusinessVO deleteBusiness(Long id);
    BusinessVO patchBusiness(Long id, BusinessUpdateDTO updateDto);
    List<BusinessVO> getBusinesses();
    BusinessVO addBusiness(BusinessDTO businessDto);
    List<BusinessSearchVO> getBusinessesBySearch(String keyword, boolean isScore, boolean isSales);
    Integer applyForAddBusiness(Business business);

    List<Business> getMerchantBusinesses(Long userId, Integer status);
    List<Business>listBusinessByOrderTypeId(Integer type);
    List<MerchantStatsVO> getBusinessIdList();
    List<BusinessSearchVO> getBusinessesInCarousel();
    BusinessVO patchBusinessOwn(Long id, BusinessUpdateDTO updateDto);
}
