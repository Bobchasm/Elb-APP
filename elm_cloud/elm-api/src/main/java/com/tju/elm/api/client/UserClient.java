package com.tju.elm.api.client;

import com.tju.elm.api.po.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient("user-service")
public interface UserClient{

    @GetMapping("/api/user/current")
    User getCurrentUser(@RequestParam String username);
}
