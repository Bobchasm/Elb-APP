package com.tju.elm.business.es;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDocument {
    private Long id;
    private String businessName;
    private String businessExplain;
    private String businessAddress;
    private String businessImg;
    private BigDecimal startPrice;
    private BigDecimal deliveryPrice;
    private Integer status;  // 1-已上线
    private Boolean isDeleted;
}