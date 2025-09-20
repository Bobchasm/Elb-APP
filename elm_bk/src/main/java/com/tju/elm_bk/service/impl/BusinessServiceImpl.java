package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.vo.BusinessSearchVO;
import com.tju.elm_bk.vo.BusinessVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessServiceImpl implements BusinessService {
    private final BusinessMapper businessMapper;
//    private final BusinessVoMapper businessVoMapper; // 注入MapStruct Mapper

    @Override
    public BusinessVO getBusinessById(Integer id) {
        System.out.println("查询商家ID: " + id);
        return businessMapper.getBusinessById(id);
    }

    @Override
    public BusinessVO updateBusiness(Integer id, BusinessUpdateDTO updateDto) {
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
    public BusinessVO deleteBusiness(Integer id) {
        BusinessVO businessVo =businessMapper.getBusinessById(id);
        int result =businessMapper.deleteBusiness(id);
        if (result == 0) {
            throw new RuntimeException("删除商户信息失败，商户不存在或已被删除");
        }
        return businessVo;

    }
    @Override
    public BusinessVO patchBusiness(Integer id, BusinessUpdateDTO updateDto) {

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

    //搜索与筛选商铺信息
    @Override
    public List<BusinessSearchVO> getBusinessesBySearch(String keyword, boolean isScore) {
        List<BusinessSearchVO> businesses = businessMapper.searchBusinesses(keyword);

        // 为每个店铺计算评分
        for (BusinessSearchVO business : businesses) {
            Map<String, Object> interactionCounts = businessMapper.getInteractionCounts(business.getId());

            int likeCount = 0;
            int collectCount = 0;

            // 安全地处理可能为null的值
            Object likeObj = interactionCounts.get("likeCount");
            Object collectObj = interactionCounts.get("collectCount");

            if (likeObj instanceof BigDecimal) {
                likeCount = ((BigDecimal) likeObj).intValue();
            } else if (likeObj instanceof Long) {
                likeCount = ((Long) likeObj).intValue();
            } else if (likeObj instanceof Integer) {
                likeCount = (Integer) likeObj;
            }

            if (collectObj instanceof BigDecimal) {
                collectCount = ((BigDecimal) collectObj).intValue();
            } else if (collectObj instanceof Long) {
                collectCount = ((Long) collectObj).intValue();
            } else if (collectObj instanceof Integer) {
                collectCount = (Integer) collectObj;
            }

            // 计算评分 (点赞权重0.6，收藏权重0.4，归一化到1-5分)
            double normalizedRating = 1 + 4 * (0.6 * likeCount / (likeCount + 10.0) + 0.4 * collectCount / (collectCount + 10.0));
            BigDecimal rating = BigDecimal.valueOf(normalizedRating).setScale(2, RoundingMode.HALF_UP);
            business.setScore(rating);
        }

        if (isScore) {
            // 按评分降序排序
            businesses.sort((b1, b2) -> b2.getScore().compareTo(b1.getScore()));
        }

        return businesses;
    }


}
