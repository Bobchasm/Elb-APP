package com.tju.elm.payment.domain.repository;


import com.tju.elm.payment.domain.model.VipInfo;

public interface VipInfoRepository {
    VipInfo findByLevel(Integer level);
}
