package com.tju.elm_bk.rich.domain.repository;

import com.tju.elm_bk.rich.domain.model.VipInfo;

public interface VipInfoRepository {
    VipInfo findByLevel(Integer level);
}
