package com.tju.elm_bk.service;

import com.tju.elm_bk.pojo.dto.AuditPermissionDTO;
import com.tju.elm_bk.pojo.dto.BusinessPermissionDTO;
import com.tju.elm_bk.pojo.entity.PermissionApplication;
import com.tju.elm_bk.pojo.vo.BusinessPermissionVO;
import com.tju.elm_bk.pojo.vo.MerchantApplicationsVO;

import java.util.List;

public interface PermissionApplicationService {
    PermissionApplication applyMerchant();

    PermissionApplication auditApplication(AuditPermissionDTO auditDTO);

    BusinessPermissionVO applyShop(BusinessPermissionDTO businessPermissionDTO);

    BusinessPermissionVO auditShopApplication(BusinessPermissionDTO businessPermissionDTO);

    List<MerchantApplicationsVO> getMerchantApplications();

    List<BusinessPermissionVO> getShopApplications();
}
