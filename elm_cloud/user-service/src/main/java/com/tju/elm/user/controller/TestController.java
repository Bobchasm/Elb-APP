package com.tju.elm.user.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String addDeliveryAddress(@RequestHeader(value = "truth", required = false) String truth) {
        log.info("truth:"+truth);

        return "test";
    }
}
