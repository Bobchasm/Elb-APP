package com.tju.elm.order.zoo.pojo.dto;

import com.tju.elm.order.zoo.pojo.vo.Order;
import com.tju.elm.order.zoo.pojo.vo.OrderDetailet;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {

    @Schema(description = "订单")
    private Order order;

    @Schema(description = "订单商品")
    private OrderDetailet orderDetailet;

}
