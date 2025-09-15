package com.tju.elm_bk.entity;

import com.alibaba.druid.support.monitor.annotation.MTable;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Schema(description = "用户ID")
    private Long id;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码")
    private String password;

    @Schema(description = "是否激活")
    private Boolean activated;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "是否删除")
    private Boolean isDeleted;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updater;

    // 关联字段
    @Schema(description = "用户拥有的权限列表")
    private List<Authority> authorities;

    @Schema(description = "用户个人信息")
    private Person person;

    @Schema(description = "用户拥有的商家列表")
    private List<Business> businesses;

    @Schema(description = "用户的配送地址列表")
    private List<DeliveryAddress> deliveryAddresses;

    @Schema(description = "用户的订单列表")
    private List<Order> orders;

    @Schema(description = "用户的购物车列表")
    private List<Cart> carts;
}
