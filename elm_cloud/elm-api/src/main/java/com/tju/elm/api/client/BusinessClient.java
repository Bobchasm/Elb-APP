package com.tju.elm.api.client;

import com.tju.elm.api.po.Business;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.util.List;
import java.util.Set;

@FeignClient("business-service")
public interface BusinessClient {
    @GetMapping("/api/businesses/remote")
    HttpResult<Business> gainBusinessById(@RequestParam Long businessId);

    @PostMapping("/api/businesses/remote/ids")
    HttpResult<List<Business>> gainBusinessByIds(@RequestBody Set<Long> businessIds);

    @GetMapping("/api/businesses/ai/keyword")
    HttpResult<List<Business>> searchByKeyword(String keyword,Integer limit);

}
