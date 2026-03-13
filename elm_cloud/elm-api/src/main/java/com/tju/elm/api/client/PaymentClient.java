package com.tju.elm.api.client;

import com.tju.elm.api.dto.TransactionDTO;
import com.tju.elm.api.vo.TransactionRecordDetailVO;
import com.tju.elm.api.vo.WalletVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.math.BigDecimal;

@FeignClient("payment-service")
public interface PaymentClient {

    @GetMapping("/api/wallet/transaction/thaw")
    HttpResult<Boolean> thaw(@RequestParam Long orderId, @RequestParam Integer status);

    @GetMapping("/api/wallet/transaction/income")
    HttpResult<Boolean> income(@RequestParam Long userId, @RequestParam Long orderId);

    @GetMapping("/api/wallet/transaction/back")
    HttpResult<Boolean> back(@RequestParam Long userId, @RequestParam BigDecimal amount);

    @GetMapping("/api/wallet/message")
    HttpResult<WalletVO> walletMessage();

    @PostMapping("/api/wallet/transaction/back")
    HttpResult<Boolean> back(@RequestBody TransactionDTO transactionDTO);

}
