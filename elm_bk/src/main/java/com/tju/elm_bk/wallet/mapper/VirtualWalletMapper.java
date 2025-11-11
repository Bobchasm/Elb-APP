package com.tju.elm_bk.wallet.mapper;

import com.tju.elm_bk.wallet.domain.infrastructure.query.WalletVipQuery;
import com.tju.elm_bk.wallet.entity.VirtualWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VirtualWalletMapper {

    Long createVirtualWallet(VirtualWallet wallet);

    @Select("select * from virtual_wallet_vip_rule where is_deleted = 0")
    List<WalletVipQuery> getVipRules();

    @Update("update virtual_wallet_vip_rule set user_id = #{userId}, status = #{status}, vip_level = #{vipLevel}, balance = #{balance}, overdraft_amount = #{overdraftAmount}, overdrawn_amount = #{overdrawnAmount} where id = #{walletId}")
    void updateWallet(Long walletId,VirtualWallet wallet);
}
