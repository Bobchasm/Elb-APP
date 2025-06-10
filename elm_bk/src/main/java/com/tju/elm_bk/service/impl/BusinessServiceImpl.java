package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.untity.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private BusinessMapper businessMapper;  // 通过Spring注入Mapper实例


    @Override
    public List<Business> listBusinessByOrderTypeId(Integer type) {
        List<Business> businessList= businessMapper.listBusinessByOrderTypeId(type);  // 通过实例调用方法
        return businessList;
    }

    @Override
    public Business getBusinessById(Integer business) {
        return businessMapper.getBusinessById(business);
    }

    @Override
    public int updateBusiness(Business business) {
        return businessMapper.updateBusiness(business);
    }

    @Override
    public Business getBusinessByIdByPass(Business business) {
        return businessMapper.getBusinessByIdByPass(business);
    }

    @Override
    public List<Business> listBusinessByBusinessName(String businessName) {
        return businessMapper.listBusinessByBusinessName(businessName);
    }

    @Override
    public int saveBusiness(Business business){
        return businessMapper.saveBusiness(business);
    }

    @Override
    public int checkBusiness(Business business) {
        return businessMapper.checkBusiness(business);
    }

    @Override
    public int getBusinessIdByPhoneNumber(Business business){
        return businessMapper.getBusinessIdByPhoneNumber(business);
    }
}