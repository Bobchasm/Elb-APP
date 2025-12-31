package com.tju.elm.payment.domain.application.service;

import com.tju.elm.payment.domain.web.vo.WalletVO;
import com.tju.elm.payment.domain.web.vo.WalletVipVO;
import com.tju.elm.payment.mapper.VirtualWalletLoanMapper;
import com.tju.elm.payment.mapper.VirtualWalletMapper;
import com.tju.elm.payment.mapper.VirtualWalletVipRuleMapper;
import exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import result.ResultCodeEnum;

import java.util.List;

@Service
public class WalletQueryService {
//    @Autowired
//    private UserMapper userMapper;
//    @Autowired
//    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private VirtualWalletMapper virtualWalletMapper;
    @Autowired
    private VirtualWalletVipRuleMapper virtualWalletVipRuleMapper;
    private VirtualWalletLoanMapper virtualWalletLoanMapper;

//    public String walletRule() {
//
//        SystemConfig s = systemConfigMapper.getConfigByKey("VIRTUAL_WALLET_RULES");
//        if (s == null) {
//            throw new APIException(ResultCodeEnum.KEY_MISSED);
//        }
//        return s.getConfigValue();
//    }
//
//    public WalletVO walletMessage() {
//        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
//        WalletVO ret =  virtualWalletMapper.queryWallet(user.getId());
//        if (null == ret) {
//            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
//        }
//        return ret;
//    }

    public List<WalletVipVO> walletVipRules() {
        return virtualWalletVipRuleMapper.getVipRules();
    }


}
