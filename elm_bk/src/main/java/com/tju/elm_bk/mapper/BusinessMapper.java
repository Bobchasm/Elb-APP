package com.tju.elm_bk.mapper;
import java.util.List;
import java.util.Map;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.entity.Authority;
import com.tju.elm_bk.vo.BusinessSearchVO;
import com.tju.elm_bk.vo.BusinessVO;
import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.vo.BusinessVO;
import org.apache.ibatis.annotations.*;


@Mapper
public interface BusinessMapper {

    BusinessVO getBusinessById(Integer businessId);

    // 更新商户信息
    int updateBusiness(@Param("id") Integer id, @Param("updateDto") BusinessUpdateDTO updateDto);

    // 更新商户所有者信息
    int updateBusinessOwner(@Param("id") Integer id, @Param("updateDto") BusinessUpdateDTO updateDto);

    // 嵌套查询方法
    List<Authority> selectAuthoritiesByUserId(@Param("userId") Integer userId);

    // 逻辑删除商户并返回删除前的信息
    @Update("UPDATE business SET is_deleted = 1 WHERE id = #{id} AND is_deleted = 0")
    int deleteBusiness(@Param("id") Integer id);

    int insertBusiness(@Param("businessDto") BusinessDTO businessDto);

    List<BusinessVO> getBusinesses();

    @Select("SELECT b.* FROM business b WHERE b.id = #{businessId)}")
    BusinessVO selectBusinessVO(Long businessId);

    @Select("SELECT b.* FROM business b WHERE b.id = #{businessId)}")
    Business selectBusinessById(Long businessId);

//    下面两个用于搜索+筛选
    List<BusinessSearchVO> searchBusinesses(@Param("keyword") String keyword);
    Map<String, Object> getInteractionCounts(@Param("businessId") Long businessId);
}
