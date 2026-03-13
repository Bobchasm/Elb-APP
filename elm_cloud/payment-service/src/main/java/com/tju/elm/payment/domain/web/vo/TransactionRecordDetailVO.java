package com.tju.elm.payment.domain.web.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordDetailVO extends TransactionRecordVO {
    @Schema(description = "操作金额是否为冻结 0-否 1-是")
    private Integer status;
    @Schema(description = "转出钱包 交易类型为充值时值为0")
    private Long fromAccount;
    @Schema(description = "转入钱包 交易类型为提现时值为0")
    private Long toAccount;
    @Schema(description = "转出钱包用户姓名 交易类型为充值时值为null")
    private String fromAccountName;
    @Schema(description = "转入钱包用户姓名 交易类型为提现时值为null")
    private String toAccountName;
    @Schema(description = "手续费率或奖励率")
    private Float feeRate;
}
