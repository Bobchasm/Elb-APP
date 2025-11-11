package com.tju.elm_bk.wallet.domain.web.controller;

import com.tju.elm_bk.result.HttpResult;
import com.tju.elm_bk.wallet.domain.application.service.WalletApplicationService;
import com.tju.elm_bk.wallet.domain.application.service.WalletQueryService;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
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

    @GetMapping("/transaction/list")
    @Operation(summary = "获取用户钱包明细列表")
    public HttpResult<List<TransactionRecordVO>> transactionList(Integer type, Integer status, LocalDate startDate, LocalDate endDate) {
        return HttpResult.success(walletApplicationService.transactionRecord(type,status,startDate,endDate));
    }

    @GetMapping("/transaction/detail")
    @Operation(summary = "根据明细id获取指定明细详细信息")
    public HttpResult<TransactionRecordDetailVO> transactionDetail(Long transactionId) {
        return HttpResult.success(walletApplicationService.transactionRecordDetail(transactionId));
    }

    @GetMapping("/transaction/detail/order")
    @Operation(summary = "根据订单id获取指定明细详细信息")
    public HttpResult<TransactionRecordDetailVO> transactionDetailByOrder(Long orderId) {
        return HttpResult.success(walletApplicationService.transactionRecordDetailByOrder(orderId));
    }

    @GetMapping("/transaction/payment")
    @Operation(summary = "使用钱包支付订单",description = "注：订单确认前金额不会到商家钱包中")
    public HttpResult<Boolean> pay(Long orderId) {
        return HttpResult.success(walletApplicationService.payOrder(orderId));
    }

    @GetMapping("/recharge")
    @Operation(summary = "充值")
    public HttpResult<Boolean> recharge(BigDecimal amount) {
        return HttpResult.success(walletApplicationService.recharge(amount));
    }

    @GetMapping("/withdrawal")
    @Operation(summary = "提现")
    public HttpResult<Boolean> withdrawal(BigDecimal amount) {
        return HttpResult.success(walletApplicationService.withdrawal(amount));
    }


}
