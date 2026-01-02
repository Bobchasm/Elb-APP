package com.tju.elm.api.client;

import com.tju.elm.api.dto.TransactionDTO;
import com.tju.elm.api.vo.TransactionRecordDetailVO;
import com.tju.elm.api.vo.WalletVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import result.HttpResult;

import java.math.BigDecimal;

@FeignClient("payment-service")
public interface PaymentClient {

    @GetMapping("/api/wallet/transaction/thaw")
    HttpResult<Boolean> thaw(Long orderId, Integer status);

    @GetMapping("/api/wallet/transaction/income")
    HttpResult<Boolean> income(Long userId,Long orderId);

    @GetMapping("/api/wallet/transaction/back")
    HttpResult<Boolean> back(Long userId, BigDecimal amount);

    @GetMapping("/api/wallet/message")
    HttpResult<WalletVO> walletMessage();

    @PostMapping("/api/wallet/transaction/back")
    HttpResult<Boolean> back(@RequestBody TransactionDTO transactionDTO);

}
