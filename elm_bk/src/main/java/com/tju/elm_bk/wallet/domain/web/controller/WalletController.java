package com.tju.elm_bk.wallet.domain.web.controller;

import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.wallet.domain.application.service.WalletApplicationService;
import com.tju.elm_bk.wallet.domain.application.service.WalletQueryService;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/wallet")
@Tag(name="虚拟钱包")
public class WalletController {
    @Autowired
    private WalletApplicationService walletApplicationService;
    @Autowired
    private WalletQueryService walletQueryService;




    @GetMapping("/rule")
    @Operation(summary = "获取虚拟钱包手续费&奖励规则")
    public HttpResult<String> walletRule() {
        return HttpResult.success(walletQueryService.walletRule());
    }

    @GetMapping("/message")
    @Operation(summary = "获取用户钱包信息",description = "未开通会抛异常")
    public HttpResult<WalletVO> walletMessage() {
        return HttpResult.success(walletQueryService.walletMessage());
    }

    @GetMapping("/vip_rule")
    @Operation(summary = "获取虚拟钱包vip规则")
    public HttpResult<List<WalletVipVO>> walletVipRule() {
        return HttpResult.success(walletQueryService.walletVipRules());
    }




}
