package com.tju.elm.payment.domain.web.controller;

import com.tju.elm.api.dto.TransactionDTO;
import com.tju.elm.payment.domain.application.service.WalletApplicationService;
import com.tju.elm.payment.domain.application.service.WalletQueryService;
import com.tju.elm.payment.domain.web.vo.*;
import com.tju.elm.payment.entity.VirtualWalletLoan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import result.HttpResult;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/wallet")
@Tag(name="虚拟钱包")
public class WalletController {
    @Autowired
    private WalletApplicationService walletApplicationService;
    @Autowired
    private WalletQueryService walletQueryService;

    @GetMapping("/open")
    @Operation(summary = "用户开通钱包")
    public HttpResult<Long> openWallet() {
        return HttpResult.success(walletApplicationService.open());
    }

    @GetMapping("/message")
    @Operation(summary = "获取用户钱包信息",description = "未开通会抛异常")
    public HttpResult<WalletVO> walletMessage() {
        return HttpResult.success(walletQueryService.walletMessage());
    }

    @GetMapping("/transaction/list")
    @Operation(summary = "获取用户钱包明细列表")
    public HttpResult<List<TransactionRecordVO>> transactionList(@RequestParam(required = false) Integer type,
                                                                 @RequestParam(required = false) Integer status,
                                                                 @RequestParam(required = false) LocalDate startDate,
                                                                 @RequestParam(required = false) LocalDate endDate) {
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

    @GetMapping("/rule")
    @Operation(summary = "获取虚拟钱包手续费&奖励规则")
    public HttpResult<String> walletRule() {
        return HttpResult.success(walletQueryService.walletRule());
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

    @GetMapping("/preview")
    @Operation(summary = "提现/充值预览",description = "返回手续费/奖励金额,option 0-充值 1-提现")
    public HttpResult<PreviewVO> preview(BigDecimal amount, Integer option) {
        return HttpResult.success(walletApplicationService.getPreview(amount,option));
    }

    @GetMapping("/vip/rule")
    @Operation(summary = "获取虚拟钱包vip规则")
    public HttpResult<List<WalletVipVO>> walletVipRule() {
        return HttpResult.success(walletQueryService.walletVipRules());
    }

    @GetMapping("/vip/apply")
    @Operation(summary = "申请vip")
    public HttpResult<Boolean> walletVipRule(Integer vipLevel) {
        return HttpResult.success(walletApplicationService.applyVip(vipLevel));
    }

    @GetMapping("/loan/repay")
    @Operation(summary = "还贷款",description = "option 0-第三方支付 1-钱包")
    public HttpResult<Boolean> repayLoan(Long id,Integer option) {
        return HttpResult.success(walletApplicationService.repayLoan(id,option));
    }

    @GetMapping("/loan/list")
    @Operation(summary = "查看钱包贷款",description = "LocalDateTime不为null就是还了的")
    public HttpResult<List<VirtualWalletLoan>> listLoan() {
        return HttpResult.success(walletApplicationService.getWalletLoanList());
    }

    @GetMapping("/loan/detail")
    @Operation(summary = "查看钱包某项贷款")
    public HttpResult<LoanVO> preLoan(Long loanId) {
        return HttpResult.success(walletApplicationService.getWalletLoanById(loanId));
    }

    @GetMapping("/transaction/thaw")
    @Operation(summary = "解冻交易")
    public HttpResult<Boolean> thaw(Long  orderId,Integer status) {
        return HttpResult.success(walletApplicationService.thawTransactionAccount(orderId,status));
    }

    @GetMapping("/transaction/income")
    @Operation(summary = "钱包入账")
    public HttpResult<Boolean> income(Long userId,Long orderId) {
        return HttpResult.success(walletApplicationService.income(userId,orderId));
    }

    @PostMapping("/transaction/back")
    @Operation(summary = "钱包退款")
    public HttpResult<Boolean> back(@RequestBody TransactionDTO transactionDTO) {
        return HttpResult.success(walletApplicationService.back(transactionDTO));
    }

}
