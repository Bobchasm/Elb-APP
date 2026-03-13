package com.tju.elm.api.dto;


import com.tju.elm.api.po.Order;
import com.tju.elm.api.po.OrderDetailet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderDTO {

    private Order order;
    private OrderDetailet orderDetailet;

}
