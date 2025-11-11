package com.tju.elm_bk.rich.domain.model.enums;

public enum WalletStatus {
    ACTIVE(0, "正常"),
    FROZEN(1, "冻结");

    private final int code;
    private final String description;

    WalletStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() { return code; }
    public String getDescription() { return description; }

    public static WalletStatus fromCode(int code) {
        for (WalletStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的钱包状态: " + code);
    }

    public boolean isActive() {
        return this == ACTIVE;
    }
}
