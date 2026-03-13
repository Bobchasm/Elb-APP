package com.tju.elm.api.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRecordDetailVO extends TransactionRecordVO {
    private Integer status;
    private Long fromAccount;
    private Long toAccount;
    private String fromAccountName;
    private String toAccountName;
    private Float feeRate;
}
