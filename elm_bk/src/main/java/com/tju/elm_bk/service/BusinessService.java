package com.tju.elm_bk.service;
import java.util.List;
import com.tju.elm_bk.untity.Business;
import org.springframework.stereotype.Service;

@Service
public interface BusinessService {
    public List<Business> listBusinessByOrderTypeId(Integer orderTypeId);
    public Business getBusinessById(Business business);
    public int updateBusiness(Business business);
    public List<Business> listBusinessByBusinessName(String businessName);
    public int saveBusiness(Business business);
    public Business getBusinessByIdByPass(Business business);
    public int checkBusiness (Business business);
    public int getBusinessIdByPhoneNumber(Business business);
}