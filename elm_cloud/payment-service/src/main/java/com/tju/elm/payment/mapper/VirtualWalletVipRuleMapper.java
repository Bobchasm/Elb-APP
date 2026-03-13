package com.tju.elm.payment.mapper;

import com.tju.elm.payment.domain.web.vo.WalletVipVO;
import com.tju.elm.payment.entity.VirtualWalletVipRule;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface VirtualWalletVipRuleMapper {
    @Select("select * from virtual_wallet_vip_rule where is_deleted = 0")
    List<WalletVipVO> getVipRules();

    @Select("select * from virtual_wallet_vip_rule where is_deleted = 0 and id = #{level}")
    VirtualWalletVipRule getVipRule(Integer level);
}
