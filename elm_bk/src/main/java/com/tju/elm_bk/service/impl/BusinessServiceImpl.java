package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.vo.BusinessVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {
    private final BusinessMapper businessMapper;
//    private final BusinessVoMapper businessVoMapper; // 注入MapStruct Mapper

    @Override
    public BusinessVO getBusinessById(Long id) {
        System.out.println("查询商家ID: " + id);
        return businessMapper.getBusinessById(id);
    }

    @Override
    public BusinessVO updateBusiness(Long id, BusinessUpdateDTO updateDto) {
        System.out.println("前端--更新商家信息为: " + updateDto);
        // 1. 更新商户基本信息
        int result = businessMapper.updateBusiness(id, updateDto);
        if (result == 0) {throw new RuntimeException("更新商户信息失败，商户不存在或已被删除");}
        // 2. 如果有商户所有者信息，更新商户所有者
        if (updateDto.getBusinessOwner() != null) {businessMapper.updateBusinessOwner(id, updateDto);}
        // 3. 重新查询完整的商户信息并返回
        return businessMapper.getBusinessById(id);
    }
    @Override
    public BusinessVO deleteBusiness(Long id) {
        BusinessVO businessVo =businessMapper.getBusinessById(id);
        int result =businessMapper.deleteBusiness(id);
        if (result == 0) {
            throw new RuntimeException("删除商户信息失败，商户不存在或已被删除");
        }
        return businessVo;

    }
    @Override
    public BusinessVO patchBusiness(Long id, BusinessUpdateDTO updateDto) {

        int result = businessMapper.updateBusiness(id, updateDto);
        if (result == 0) {
            throw new RuntimeException("更新商户信息失败，商户不存在或已被删除");
        }

        // 2. 如果有商户所有者信息，更新商户所有者
        if (updateDto.getBusinessOwner() != null) {
            businessMapper.updateBusinessOwner(id, updateDto);
        }
        return businessMapper.getBusinessById( id);
    }

    @Override
    public BusinessVO addBusiness(BusinessDTO businessDTO) {
        //先查id是否在users表里面
        //-----------------------需要调用user的接口------------------!!!

        int result =businessMapper.insertBusiness(businessDTO);
        if (result == 0) {
            throw new RuntimeException("添加商户信息失败");
        }
        return businessMapper.getBusinessById(businessDTO.getId());
    }

    @Override
    public List<BusinessVO> getBusinesses() {
        List<BusinessVO> businesses = businessMapper.getBusinesses();
        if (businesses == null) {
            throw new APIException(ResultCodeEnum.NOT_FOUND);
        }
        return businessMapper.getBusinesses();
    }
}
