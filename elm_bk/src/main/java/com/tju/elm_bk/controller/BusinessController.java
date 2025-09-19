package com.tju.elm_bk.controller;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.vo.BusinessVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/businesses")
@RequiredArgsConstructor
@Validated
public class BusinessController {

    private final BusinessService businessService;

    /**
     * 根据ID获取店铺详情
     * @param id 店铺ID (路径参数)
     * @return 店铺详细信息
     */
    @GetMapping("/{id}")
    public HttpResult<BusinessVO> getBusiness(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            log.warn("获取店铺详情请求参数错误: id={}", id);
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        BusinessVO businessVo = businessService.getBusinessById(id);
        if (businessVo == null) {
            log.info("未找到对应的店铺信息: id={}", id);
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        System.out.println("查询到的BusinessVO对象: " + businessVo);
        log.debug("成功获取店铺详情: id={}, name={}", id, businessVo.getBusinessName());
        return HttpResult.success(businessVo);
    }

    /**
     * 更新店铺信息
     * @param id 店铺ID (路径参数)
     * @param updateDto 更新数据
     * @return 更新后的店铺信息
     */
    @PutMapping("/{id}")
    public HttpResult<BusinessVO> updateBusiness(@PathVariable("id") Integer id,@RequestBody BusinessUpdateDTO updateDto){
        if (id == null || id <= 0) {
            log.warn("更新店铺信息请求参数错误: id={}", id);
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }

        BusinessVO businessVo = businessService.updateBusiness(id, updateDto);
        return HttpResult.success(businessVo);
    }

     @DeleteMapping("/{id}")
    public HttpResult<BusinessVO> deleteBusiness(@PathVariable("id") Integer id) {
        if (id == null || id <= 0) {
            log.warn("删除店铺信息请求参数错误: id={}", id);
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
         BusinessVO businessVo = businessService.deleteBusiness(id);
        return HttpResult.success(businessVo);
    }
     @PatchMapping("/{id}")
    public HttpResult<BusinessVO> patchBusiness(@PathVariable("id") Integer id,@RequestBody BusinessUpdateDTO updateDto) {
        if (id == null || id <= 0) {
            log.warn("更新店铺信息请求参数错误: id={}", id);
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
         BusinessVO businessVo = businessService.patchBusiness(id,updateDto);
        return HttpResult.success(businessVo);
    }

    /**
     * 获取所有店铺信息
     * @return 所有店铺信息
     */
    //  【【----------------------- 后续加上分页查询，别忘了-------------------------】】
    @GetMapping
    public HttpResult<List<BusinessVO>> getBusinesses() {
        List<BusinessVO> businessVos = businessService.getBusinesses();
        return HttpResult.success(businessVos);
    }


     @PostMapping
    public HttpResult<BusinessVO> addBusiness(@RequestBody BusinessDTO businessDTO) {
        BusinessVO businessVo = businessService.addBusiness(businessDTO);
        return HttpResult.success(businessVo);
    }





}