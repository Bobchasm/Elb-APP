package com.tju.elm_bk.rich.domain.application.service;

import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.SystemConfigMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.pojo.entity.SystemConfig;
import com.tju.elm_bk.pojo.entity.User;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.rich.domain.model.Wallet;
import com.tju.elm_bk.rich.entity.VirtualWallet;
import com.tju.elm_bk.rich.entity.VirtualWalletLoan;
import com.tju.elm_bk.rich.mapper.VirtualWalletLoanMapper;
import com.tju.elm_bk.rich.mapper.VirtualWalletVipRuleMapper;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.rich.domain.web.vo.WalletVO;
import com.tju.elm_bk.rich.domain.web.vo.WalletVipVO;
import com.tju.elm_bk.rich.mapper.VirtualWalletMapper;
import com.tju.elm_bk.rich.mapper.VirtualWalletTransactionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WalletQueryService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    @Autowired
    private VirtualWalletMapper virtualWalletMapper;
    @Autowired
    private VirtualWalletVipRuleMapper virtualWalletVipRuleMapper;

    public String walletRule() {

        SystemConfig s = systemConfigMapper.getConfigByKey("VIRTUAL_WALLET_RULES");
        if (s == null) {
            throw new APIException(ResultCodeEnum.KEY_MISSED);
        }
        return s.getConfigValue();
    }

    public WalletVO walletMessage() {
        User user = userMapper.findByUsernameWithAuthorities(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        WalletVO ret =  virtualWalletMapper.queryWallet(user.getId());
        if (null == ret) {
            throw new APIException(ResultCodeEnum.VIRTUAL_WALLET_MISSED);
        }
        return ret;
    }

    public List<WalletVipVO> walletVipRules() {
        return virtualWalletVipRuleMapper.getVipRules();
    }


}
