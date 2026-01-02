package com.tju.elm.api.client;

import com.tju.elm.api.po.PointsExchangeOrder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.math.BigDecimal;


@FeignClient("point-service")
public interface PointClient {

    @GetMapping("/api/points/account/create")
    HttpResult<Long> createAccount(@RequestParam Long userId);

    @GetMapping("/api/points/type/count")
    HttpResult<Long> countPointsByType(@RequestParam String behaviorType);

    @GetMapping("/api/points/deductible-amount")
    HttpResult<java.math.BigDecimal> calculateDeductibleAmount(
            Long customerId, @RequestParam BigDecimal orderAmount);

    @GetMapping("/api/points/exchange-ratio")
    HttpResult<java.math.BigDecimal> getExchangeRatio();

    @GetMapping("/api/points/freeze")
    HttpResult<Boolean> freezePoints(@RequestParam Long userId,
                                    @RequestParam Long points,
                                    @RequestParam Long orderId);

    @GetMapping("/api/points/deduct_rozen")
    HttpResult<Boolean> deductFrozenPoints(@RequestParam Long userId, @RequestParam Long orderId);

    @GetMapping("/api/points/unfreeze")
    HttpResult<Boolean> unfreezeRewardPoints(@RequestParam Long userId, @RequestParam Long orderId);

    @GetMapping("/api/points/exchange_order")
    HttpResult<PointsExchangeOrder> gainByOrderId(@RequestParam Long orderId);

    @GetMapping("/api/points/update_status")
    HttpResult<Boolean> updateOrderStatusFromPoint(@RequestParam Long orderId,@RequestParam Integer status);

    @GetMapping("/api/points/unfreeze/cancel")
    HttpResult<Boolean> unfreezePointsCanceled(@RequestParam Long userId, @RequestParam Long orderId);

    @GetMapping("/api/points/cancel/cancel")
    HttpResult<Boolean> cancelRewardPoints(@RequestParam Long userId, @RequestParam Long orderId);


    @GetMapping("/api/points/vip/update")
    HttpResult<Boolean> updateVip(@RequestParam Long userId, @RequestParam Integer newMemberLevel);
}
