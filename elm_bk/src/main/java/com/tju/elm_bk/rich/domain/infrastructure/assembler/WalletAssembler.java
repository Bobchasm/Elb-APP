package com.tju.elm_bk.rich.domain.infrastructure.assembler;

import com.tju.elm_bk.rich.domain.model.OverdraftLimit;
import com.tju.elm_bk.rich.domain.model.VipInfo;
import com.tju.elm_bk.rich.domain.model.Wallet;
import com.tju.elm_bk.rich.domain.model.enums.VipLevel;
import com.tju.elm_bk.rich.domain.model.enums.WalletStatus;
import com.tju.elm_bk.rich.entity.VirtualWallet;
import org.springframework.stereotype.Component;

import com.tju.elm_bk.rich.domain.model.Balance;

import java.math.BigDecimal;

@Component
public class WalletAssembler {
    /**
     * Wallet领域对象 → VirtualWallet持久化对象
     */
    public VirtualWallet toPO(Wallet wallet) {
        if (wallet == null) return null;
        VirtualWallet po = new VirtualWallet();
        po.setId(wallet.getId());
        po.setUserId(wallet.getUserId());
        po.setBalance(wallet.getBalance().getAmount());
        po.setOverdraftAmount(wallet.getOverdrawnAmount());
        po.setStatus(wallet.getStatus().getCode());
        po.setVipLevel(wallet.getVipInfo().getLevel().getCode());
        po.setCreateTime(wallet.getCreateTime());
        po.setUpdateTime(wallet.getUpdateTime());
        return po;
    }

    /**
     * VirtualWallet持久化对象 → Wallet领域对象
     */
    public Wallet toDomain(VirtualWallet po) {
        if (po == null) return null;

        Wallet wallet = new Wallet(po.getUserId());
        setId(wallet, po.getId());
        setField(wallet, "balance", new Balance(po.getBalance()));
        setField(wallet, "overdraftLimit", new OverdraftLimit(po.getOverdraftAmount() != null ? po.getOverdraftAmount() : BigDecimal.ZERO));
        setField(wallet, "overdrawnAmount", po.getOverdraftAmount() != null ? po.getOverdraftAmount() : BigDecimal.ZERO);
        setField(wallet, "status", WalletStatus.fromCode(po.getStatus()));
        setField(wallet, "vipInfo", new VipInfo(VipLevel.fromCode(po.getVipLevel())));
        setField(wallet, "createTime", po.getCreateTime());
        setField(wallet, "updateTime", po.getUpdateTime());
        return wallet;
    }



    /**
     * 设置领域对象ID（反射方式）
     */
    private void setId(Wallet wallet, Long id) {
        try {
            java.lang.reflect.Field idField = Wallet.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(wallet, id);
        } catch (Exception e) {
            throw new RuntimeException("设置Wallet ID失败", e);
        }
    }

    /**
     * 设置领域对象属性（反射方式）
     */
    private void setField(Object target, String fieldName, Object value) {
        try {
            java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new RuntimeException("设置字段失败: " + fieldName, e);
        }
    }

}
