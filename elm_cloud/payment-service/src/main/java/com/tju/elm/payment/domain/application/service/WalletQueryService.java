package com.tju.elm.payment.domain.application.service;

import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.po.SystemConfig;
import com.tju.elm.api.po.User;
import com.tju.elm.payment.domain.web.vo.WalletVO;
import com.tju.elm.payment.domain.web.vo.WalletVipVO;
import com.tju.elm.payment.mapper.VirtualWalletLoanMapper;
import com.tju.elm.payment.mapper.VirtualWalletMapper;
import com.tju.elm.payment.mapper.VirtualWalletVipRuleMapper;
import exception.APIException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import result.ResultCodeEnum;
import utils.UserContext;

import java.util.List;

@Service
public class WalletQueryService {

    @Autowired
    private VirtualWalletMapper virtualWalletMapper;
    @Autowired
    private VirtualWalletVipRuleMapper virtualWalletVipRuleMapper;

    @Autowired
    private UserClient userClient;

    public String walletRule() {

        SystemConfig s = userClient.getSystemConfig("VIRTUAL_WALLET_RULES").getData();
        if (s == null) {
            throw new APIException(ResultCodeEnum.KEY_MISSED);
        }
        return s.getConfigValue();
    }

    public WalletVO walletMessage() {
        User user = getCurrentUser();
        WalletVO ret =  virtualWalletMapper.queryWallet(user.getId());

        ret.setUsername(user.getUsername());

        return ret;
    }

    public List<WalletVipVO> walletVipRules() {
        return virtualWalletVipRuleMapper.getVipRules();
    }


    /**
     * 获取当前用户ID
     */
    private User getCurrentUser() {
        return userClient.getUserByName(UserContext.getUsername()).getData();
    }
}
