package com.tju.elm.point.zoo.pojo.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 积分兑换消息DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointsExchangeMessageDTO {

    /**
     * 唯一请求ID（用于幂等性）
     */
    private String requestId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long foodId;

    /**
     * 兑换数量
     */
    private Integer quantity;

    /**
     * 所需积分
     */
    private Long requiredPoints;

    /**
     * 配送地址ID
     */
    private Long addressId;

    /**
     * 预扣时间
     */
    private LocalDateTime preDeductTime;

    /**
     * 重试次数
     */
    private Integer retryCount = 0;

    /**
     * 增加重试次数
     */
    public void incrementRetryCount() {
        this.retryCount++;
    }
}