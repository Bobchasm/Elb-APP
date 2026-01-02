package com.tju.elm.api.client;

import com.tju.elm.api.po.Business;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

@FeignClient("business-service")
public interface BusinessClient {
    @GetMapping("/remote")
    HttpResult<Business> gainBusinessById(@RequestParam Long businessId);

}
