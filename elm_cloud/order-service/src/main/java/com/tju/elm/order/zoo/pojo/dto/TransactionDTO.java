package com.tju.elm.order.zoo.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bouncycastle.cert.dane.DANEEntry;

import javax.swing.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionDTO {
    @Schema(description = "出用户")
    private Long fromUserId;
    @Schema(description = "入用户")
    private Long toUserId;
    @Schema(description = "订单id")
    private Long orderId;
}
