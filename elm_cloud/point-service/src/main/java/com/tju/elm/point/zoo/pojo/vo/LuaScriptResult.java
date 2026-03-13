package com.tju.elm.point.zoo.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LuaScriptResult {
    private Integer code;
    private String msg;
    private String requestId;

    public boolean isSuccess() {
        return code != null && code == 1;
    }
}
