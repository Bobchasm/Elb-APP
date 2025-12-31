package com.tju.elm.point.zoo.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 营销积分规则与商品关联实体
 * 用于支持一个促销积分规则关联多个商品
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "营销积分规则与商品关联实体")
public class MarketingPointsRuleFood {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "积分规则ID（关联marketing_points_rule表）")
    private Long ruleId;

    @Schema(description = "商品ID（关联food表）")
    private Long foodId;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "是否删除")
    @JsonProperty("deleted")
    private Boolean isDeleted;
}

