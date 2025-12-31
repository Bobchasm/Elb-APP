package com.tju.elm.point.zoo.pojo.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "积分账户VO")
public class PointsAccountVO {
    @Schema(description = "主键ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "总积分余额")
    private Long totalPoints;

    @Schema(description = "可用积分余额")
    private Long availablePoints;

    @Schema(description = "冻结积分")
    private Long frozenPoints;

    @Schema(description = "会员等级 0-普通 1-白银 2-黄金 3-钻石")
    private Integer memberLevel;

    @Schema(description = "会员等级名称")
    private String memberLevelName;

    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

