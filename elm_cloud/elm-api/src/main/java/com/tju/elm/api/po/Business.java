package com.tju.elm.api.po;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Business {
    private Long id;
    private String businessName;
    private String businessAddress;
    private String businessExplain;
    private String businessImg;
    private BigDecimal deliveryPrice;
    private BigDecimal startPrice;
    private Integer orderTypeId;
    private String remarks;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    private Long creator;
    @JsonProperty("deleted")
    private Boolean is_deleted;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
    private Long updater;
    private Long userId;
    private Integer status;

}