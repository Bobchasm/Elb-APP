package com.tju.elm_bk.dto;

import com.tju.elm_bk.vo.OrderFoodVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCreateDTO {
    @Schema(description = "商家ID")
    private Long businessId;

    @Schema(description = "地址ID")
    private Long addressId;

    @Schema(description = "订单食品Id列表")
    private List<OrderFoodVO> foodList;

    public Boolean verify() {
        return businessId != null && addressId != null;
    }
}
