package com.tju.elm.api.po;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private BigDecimal orderTotal;
    private Integer orderState;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime orderDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Long creator;
    private Boolean isDeleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Long updater;
    private Long customerId;
    private Long businessId;
    private Long addressId;
    private BigDecimal deliveryPrice;
    private Integer paymentMethod;
    private String address;
    private Integer contactSex;
    private String contactName;
    private String contactTel;
    private Long pointsUsed;
    private Long pointsAmount;
    private BigDecimal pointsDiscountAmount;

}
