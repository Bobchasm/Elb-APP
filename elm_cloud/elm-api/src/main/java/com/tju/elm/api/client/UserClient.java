package com.tju.elm.api.client;

import com.tju.elm.api.po.DeliveryAddress;
import com.tju.elm.api.po.Person;
import com.tju.elm.api.po.SystemConfig;
import com.tju.elm.api.po.User;
import com.tju.elm.api.vo.CartItemVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import result.HttpResult;

import java.util.List;
import java.util.Set;


@FeignClient("user-service")
public interface UserClient{

    @GetMapping("/api/user/current")
    HttpResult<User> getUserByName(@RequestParam String username);

    @GetMapping("/api/user/exist")
    HttpResult<Integer> hasUser(@RequestParam Long userId);

    @PostMapping("/api/user/ids")
    HttpResult<List<User>> getUserListByIds(@RequestBody Set<Long> userIds);

    @GetMapping("/api/user/id")
    HttpResult<User> gainUserById(@RequestParam Long userId);

    @PostMapping("/api/addresses/ids")
    HttpResult<List<DeliveryAddress>> gainAddressListByIds(@RequestBody Set<Long> addressIds);

    @GetMapping("/api/addresses/getDeliveryAddressById")
    HttpResult<DeliveryAddress> getDeliveryAddressById(@RequestParam Long id);


    @GetMapping("/api/person/id")
    ResponseEntity<Person> gainActualPerson(@RequestParam Long userId);



    @GetMapping("/api/config/key")
    HttpResult<SystemConfig> getSystemConfig(@RequestParam String key);
}
