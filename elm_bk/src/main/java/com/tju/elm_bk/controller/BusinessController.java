package com.tju.elm_bk.controller;

import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.untity.Business;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/BusinessController")
public class BusinessController {

    // 通过Spring自动注入Service实例
    @Autowired
    private BusinessService businessService;

    /**
     * 根据点餐分类编号查询商家信息
     * @param orderTypeId 分类编号
     * @return 商家列表
     */
    @GetMapping("/listBusinessByOrderTypeId")
    public List<Business> listBusinessByOrderTypeId(@RequestParam Integer orderTypeId) {
        return businessService.listBusinessByOrderTypeId(orderTypeId);
    }

    /**
     * 根据商家编号查询商家信息
     * @param business 商家编号
     * @return 商家详情
     */
    @PostMapping("/getBusinessById")
    public Business getBusinessById(@RequestBody Business business) {
        return businessService.getBusinessById(business);
    }

    @RequestMapping("/updateBusiness")
    public int updateBusiness(@RequestBody Business business) throws Exception {
        return businessService.updateBusiness(business);
    }

    @RequestMapping("/saveBusiness")
    public int saveBusiness(@RequestBody Business business) throws Exception{
        return businessService.saveBusiness(business);
    }

    @PostMapping("/getBusinessIdByPhoneNumber")
    public int getBusinessIdByPhoneNumber(@RequestBody Business business) throws Exception{
        return businessService.getBusinessIdByPhoneNumber(business);
    }

    @RequestMapping("/checkBusiness")
    public int checkBusiness (@RequestBody Business business) throws Exception{
        return businessService.checkBusiness(business);
    }

    @PostMapping("/getBusinessByIdByPass")
    public Business getBusinessByIdByPass(@RequestBody Business business) {
        return businessService.getBusinessByIdByPass(business);
    }

    @RequestMapping("/listBusinessByBusinessName")
    public List<Business> listBusinessByBusinessName(@RequestBody Business business) throws Exception {
        return businessService.listBusinessByBusinessName(business.getBusinessName());
    }
}