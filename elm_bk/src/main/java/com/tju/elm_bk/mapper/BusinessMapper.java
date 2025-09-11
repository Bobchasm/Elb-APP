package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.entity.Business;
import org.apache.ibatis.annotations.*;


@Mapper
public interface BusinessMapper {
    @Select("""
    select 
    businessId,businessName,businessAddress,
    businessExplain,businessImg,orderTypeId,
    starPrice,deliveryPrice,remarks 
    from elm.business where orderTypeId=#{orderTypeId}
    """)
    List<Business> listBusinessByOrderTypeId(Integer orderTypeId);

    @Select("""
    select 
    businessId,businessName,businessAddress,
    businessExplain,businessImg,orderTypeId,
    starPrice,deliveryPrice,remarks 
    from elm.business where businessId= #{businessId}
    """)
    Business getBusinessById(Integer businessId);

    @Update("UPDATE business b SET b.businessAddress =#{businessAddress},"
            + " b.businessExplain =#{businessExplain},"
            + " b.businessImg =#{businessImg},"
            + " b.businessName =#{businessName},"
            + " b.starPrice =#{starPrice},"
            + " b.deliveryPrice =#{deliveryPrice},"
            + " b.orderTypeId =#{orderTypeId}"
            + " WHERE b.businessId =#{businessId}")
    public int updateBusiness(Business business);

    @Update("UPDATE business_user a SET a.businessAddress =#{businessAddress}, a.businessExplain =#{businessExplain},  a.businessName =#{businessName},"
            + " a.starPrice =#{starPrice},"
            + " a.deliveryPrice =#{deliveryPrice}"
            + " WHERE a.businessId =#{businessId}")
            public int updateBusinessUser(Business business);

    @Select("SELECT * FROM elm.business where businessId in\r\n" +
            "(SELECT business.businessId FROM elm.food right join elm.business on food.businessId=business.businessId "
            + "where businessName like concat('%',#{businessOrFoodName},'%') or foodName like concat('%',#{businessOrFoodName},'%'))")
    public List<Business> listBusinessByBusinessName(String businessOrFoodName);


    @Select("SELECT * FROM business WHERE businessName LIKE CONCAT('%',#{businessName},'%')")
    public List<Business> listBusinessBySearchName(String businessName);

    @Select("SELECT * FROM business WHERE businessName LIKE CONCAT('%',#{category},'%') OR " +
            "remarks LIKE CONCAT('%',#{category},'%')")
    public List<Business> listBusinessByCategoryName(String category);

    @Select("select * from business_user where phoneNumber=#{phoneNumber} and password=#{password}")
    public Business getBusinessByIdByPass(Business business);

    @Select("select count(*) from business_user where phoneNumber=#{phoneNumber}")
    public int checkBusiness(Business business);

    @Select("select businessId from business_user where phoneNumber=#{phoneNumber}")
    public int getBusinessIdByPhoneNumber(Business business);

    @Insert("insert into business_user(phoneNumber,password) values(#{phoneNumber},#{password})")
    @Options(useGeneratedKeys=true,keyProperty="businessId",keyColumn="businessId")
    public int saveBusiness(Business business);

    @Insert("insert into business (businessId,businessName,businessAddress,businessExplain,businessImg,orderTypeId,starPrice,deliveryPrice) " +
            "values(#{businessId},#{businessName},#{businessAddress},#{businessExplain},#{businessImg},#{orderTypeId},#{starPrice},#{deliveryPrice})")
    public int saveBusinessMsg(Business business);

    @Select("select * from business_user where phoneNumber=#{phoneNumber}")
    Business getBusinessByPhoneNumber(String phoneNumber);
}
