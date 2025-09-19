package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.dto.BusinessDTO;
import com.tju.elm_bk.dto.BusinessUpdateDTO;
import com.tju.elm_bk.entity.Authority;
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

   /**
    List<Business> listBusinessByOrderTypeId(Integer orderTypeId);


    List<Business> listBusinessByCategoryName(String category);

    @Select("SELECT * FROM business_user WHERE phoneNumber = #{phoneNumber} AND password = #{password}")
    Business getBusinessByIdByPass(Business business);

    @Select("SELECT COUNT(*) FROM business_user WHERE phoneNumber = #{phoneNumber}")
    int checkBusiness(Business business);

//    @Select("SELECT businessId FROM business_user WHERE phoneNumber = #{phoneNumber}")
//    int getBusinessIdByPhoneNumber(Business business);

    List<Business> listBusinessBySearchName(String businessName);

//
//    @Insert("INSERT INTO business_user(phoneNumber, password) VALUES(#{phoneNumber}, #{password})")
//    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "businessId")
//    int saveBusiness(Business business);

    @Insert("INSERT INTO business (businessId, businessName, businessAddress, businessExplain, businessImg, orderTypeId, starPrice, deliveryPrice) VALUES(#{id}, #{businessName}, #{businessAddress}, #{businessExplain}, #{businessImg}, #{orderTypeId}, #{startPrice}, #{deliveryPrice})")
    int saveBusinessMsg(Business business);
    
    @Update("UPDATE business_user SET businessAddress = #{businessAddress}, businessExplain = #{businessExplain}, businessName = #{businessName}, starPrice = #{startPrice}, deliveryPrice = #{deliveryPrice} WHERE businessId = #{id}")
    int updateBusinessUser(Business business);
    
    // 根据商家名称或商品名称搜索（复杂JOIN查询）
    List<Business> listBusinessByBusinessName(String businessOrFoodName);
*/

}
