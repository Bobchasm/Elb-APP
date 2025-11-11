package com.tju.elm_bk.wallet.domain.web.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletVO {
    @Schema(description = "虚拟钱包id")
    private Long id;
    @Schema(description = "所属用户id")
    private Long user_id;
    @Schema(description = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime create_time;
    @Schema(description = "钱包状态 0-正常 1-冻结")
    private Integer status;
    @Schema(description = "vip级别 0-非vip")
    private Integer vip_level;
    @Schema(description = "余额")
    private BigDecimal balance;
    @Schema(description = "可透支金额")
    private BigDecimal overdraft_amount;
    @Schema(description = "已透支金额")
    private BigDecimal overdrawn_amount;

    @Schema(description = "所属用户名")
    private Long username;
    @Schema(description = "vip名")
    private String vipName;
    @Schema(description = "vip描述")
    private String vipDescription;

}
