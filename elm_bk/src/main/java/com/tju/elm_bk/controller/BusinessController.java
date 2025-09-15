package com.tju.elm_bk.controller;

import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.entity.Business;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Tag(name = "商家接口")
@RestController
@RequestMapping("/api/businesses")
public class BusinessController {

    @Autowired
    private BusinessService businessService;

    /**
     * 根据点餐分类编号查询商家信息
     * @param orderTypeId 含分类编号
     * @return 商家列表
     */
    @Operation(summary = "根据点餐分类编号查询商家信息")
    @GetMapping("/listBusinessByOrderTypeId")
    public List<Business> listBusinessByOrderTypeId(@RequestParam Integer orderTypeId) {
        return businessService.listBusinessByOrderTypeId(orderTypeId);
    }

    /**
     * 根据商家编号查询商家信息
     * @param business 商家编号
     * @return 商家详情
     */
    @Operation(summary = "根据商家编号查询商家信息")
    @PostMapping("/getBusinessById")//
    public Business getBusinessById(@RequestBody Business business) {
        return businessService.getBusinessById(business);
    }

    @Operation(summary = "更新商家信息")
    @PostMapping("/updateBusiness")
    public int updateBusiness(@RequestBody Business business) throws Exception {
        return businessService.updateBusiness(business);
    }

    @Operation(summary = "新增商家")
    @PostMapping("/saveBusiness")
    public Result saveBusiness(@RequestBody Business business) throws Exception{
        return businessService.saveBusiness(business);
    }

    @Operation(summary = "使用手机号获取商家id")
    @PostMapping("/getBusinessIdByPhoneNumber")
    public int getBusinessIdByPhoneNumber(@RequestBody Business business) throws Exception{
        return businessService.getBusinessIdByPhoneNumber(business);
    }

    @Operation(summary = "验证商家是否存在",description = "没看出来这个是干啥的")
    @PostMapping("/checkBusiness")
    public int checkBusiness (@RequestBody Business business) throws Exception{
        return businessService.checkBusiness(business);
    }

    @Operation(summary = "通过手机号&密码获取商家")
    @PostMapping("/getBusinessByIdByPass")
    public Business getBusinessByIdByPass(@RequestBody Business business) {
        return businessService.getBusinessByIdByPass(business);
    }

    @Operation(summary = "通过商家名称搜索商家")
    @PostMapping("/listBusinessByBusinessName")
    public List<Business> listBusinessByBusinessName(@RequestBody Business business) throws Exception {
        return businessService.listBusinessByBusinessName(business.getBusinessName());
    }

    @Operation(summary = "退出登录")
    @PostMapping("/logout")
    public Result logout() {
        return businessService.logout();
    }
}