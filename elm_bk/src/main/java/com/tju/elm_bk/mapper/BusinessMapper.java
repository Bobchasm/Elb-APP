package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessPermissionDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.entity.Authority;
import com.tju.elm_bk.vo.BusinessPermissionVO;
import com.tju.elm_bk.vo.BusinessVO;
import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.vo.BusinessVO;
import org.apache.ibatis.annotations.*;


@Mapper
public interface BusinessMapper {

    BusinessVO getBusinessById(Integer businessId);

    @Select("SELECT * FROM business WHERE id = #{id}")
    BusinessPermissionVO getBusinessPermissionById(Long businessId);

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

    @Update("UPDATE business SET status = #{status},update_time=NOW(),updater=#{updater} WHERE id = #{id}")
    void updateBusinessStatus(BusinessPermissionDTO businessPermissionDTO);

    void insertBusinessPermission(BusinessPermissionDTO businessPermissionDTO);

    @Select("SELECT COUNT(*) FROM business WHERE is_deleted = 0 AND status = 1")
    Integer count();

    @Select("SELECT * FROM business WHERE status = 0 AND is_deleted = 0")
    List<BusinessPermissionVO> listNotAudited();
}
