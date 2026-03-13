package com.tju.elm.api.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletVO {
    private Long id;
    private Long userId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    private LocalDateTime createTime;
    private Integer status;
    private Integer vipLevel;
    private BigDecimal balance;
    private BigDecimal overdraftAmount;
    private BigDecimal overdrawnAmount;

    private String username;
    private String vipName;
    private String vipDescription;
    private BigDecimal overdraftLimit;

}
