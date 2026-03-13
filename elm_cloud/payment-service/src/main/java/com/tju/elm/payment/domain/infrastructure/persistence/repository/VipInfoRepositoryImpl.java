package com.tju.elm.payment.domain.infrastructure.persistence.repository;

import com.tju.elm.payment.domain.model.VipInfo;
import com.tju.elm.payment.domain.repository.VipInfoRepository;
import com.tju.elm.payment.entity.VirtualWalletVipRule;
import com.tju.elm.payment.mapper.VirtualWalletVipRuleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class VipInfoRepositoryImpl implements VipInfoRepository {
    @Autowired
    private VirtualWalletVipRuleMapper virtualWalletVipRuleMapper;

    @Override
    public VipInfo findByLevel(Integer level) {
        VirtualWalletVipRule vipEntity = virtualWalletVipRuleMapper.getVipRule(level);
        if (vipEntity == null) {
            return null;
        }
        return new VipInfo(vipEntity.getId(),vipEntity.getCost(),vipEntity.getOverdraftLimit());
    }


}
