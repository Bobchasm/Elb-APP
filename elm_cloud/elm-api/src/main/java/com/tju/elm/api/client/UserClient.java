package com.tju.elm.api.client;

import com.tju.elm.api.po.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.util.List;


@FeignClient("user-service")
public interface UserClient{

    @GetMapping("/api/user/current")
    HttpResult<User> getUserByName(@RequestParam String username);

    @GetMapping("/user/exist")
    HttpResult<Integer> hasUser(Long userId);
}
