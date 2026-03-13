package com.tju.elm.business.mapper;


import com.tju.elm.business.pojo.entity.MerchantInteraction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
// MerchantInteractionMapper.java
@Mapper
public interface MerchantInteractionMapper {
    // 插入互动记录
    int insert(MerchantInteraction interaction);

    // 更新互动记录
    int update(MerchantInteraction interaction);

    // 根据用户和商家查询互动记录
    MerchantInteraction selectByUserAndMerchant(@Param("userId") Long userId, @Param("merchantId") Long merchantId);


    // 获取某用户收藏的商家id列表
    @Select("SELECT merchant_id FROM merchant_interaction WHERE user_id = #{userId} AND collected = 1")
    List<Long> selectUserCollectionIds(@Param("userId") Long userId);
    // 统计商铺的点赞数
    Integer countLikesByMerchantId(@Param("merchantId") Long merchantId);

    // 统计商铺的收藏数
    Integer countCollectionsByMerchantId(@Param("merchantId") Long merchantId);

    // 根据商铺id获取商铺名
    @Select("SELECT business_name FROM business WHERE id = #{merchantId}")
    String selectMerNameById(@Param("merchantId") Long merchantId);

}