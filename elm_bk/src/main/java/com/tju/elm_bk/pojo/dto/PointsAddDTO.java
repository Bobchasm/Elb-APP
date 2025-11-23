package com.tju.elm_bk.pojo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "增加积分DTO")
public class PointsAddDTO {
    @Schema(description = "用户ID", required = true)
    private Long userId;

    @Schema(description = "积分数量", required = true)
    private Long points;

    @Schema(description = "积分来源 0-消费积分 1-促销积分 2-等级积分 3-行为积分", required = true)
    private Integer pointsSource;

    @Schema(description = "关联订单ID")
    private Long relatedOrderId;

    @Schema(description = "关联商品ID")
    private Long relatedFoodId;

    @Schema(description = "关联积分规则ID")
    private Long relatedRuleId;

    @Schema(description = "积分过期时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;

    @Schema(description = "交易描述")
    private String description;
}

