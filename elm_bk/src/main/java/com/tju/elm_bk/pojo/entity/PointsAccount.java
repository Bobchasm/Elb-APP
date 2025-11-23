package com.tju.elm_bk.pojo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分账户实体")
public class PointsAccount {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID（关联users表）")
    private Long userId;

    @Schema(description = "总积分余额")
    private Long totalPoints;

    @Schema(description = "可用积分余额")
    private Long availablePoints;

    @Schema(description = "冻结积分（用于订单处理中）")
    private Long frozenPoints;

    @Schema(description = "会员等级 0-普通 1-白银 2-黄金 3-钻石")
    private Integer memberLevel;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "创建人ID")
    private Long creator;

    @Schema(description = "是否删除")
    @JsonProperty("deleted")
    private Boolean isDeleted;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    @Schema(description = "更新人ID")
    private Long updater;
}

