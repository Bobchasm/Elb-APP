package com.tju.elm.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CartItemVO {
    private Long id;
    private Integer quantity;
    private Long businessId;
    private String businessName;
    private Long foodId;
    private String foodImg;
    private String foodName;
    private Double foodPrice;
}
