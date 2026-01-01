package com.tju.elm.api.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;


@FeignClient("point-service")
public interface PointClient {

    @GetMapping("/api/points/account/create")
    HttpResult<Long> createAccount(@RequestParam Long userId);
}
