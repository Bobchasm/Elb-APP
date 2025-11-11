package com.tju.elm_bk.wallet.mapper;

import com.tju.elm_bk.wallet.domain.web.vo.WalletVO;
import com.tju.elm_bk.wallet.domain.web.vo.WalletVipVO;
import com.tju.elm_bk.wallet.entity.VirtualWallet;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface VirtualWalletMapper {

    Long createVirtualWallet(VirtualWallet wallet);

    @Select("""
        select vm.*,
            u.username,
            vr.name as vipName, vr.description as vipDescription
        from virtual_wallet vm
        left join users u on vm.user_id = u.id
        left join virtual_wallet_vip_rule vr on vm.vip_level = vr.id
        where vm.user_id = #{userId}
    """)
    WalletVO queryWallet(Long userId);

    @Select("select * from virtual_wallet where user_id = #{userId} and is_deleted = 0")
    VirtualWallet getWalletById(Long userId);

    @Select("select * from virtual_wallet_vip_rule where is_deleted = 0")
    List<WalletVipVO> getVipRules();

    @Update("update virtual_wallet_vip_rule set status = #{status}, vip_level = #{vipLevel}, balance = #{balance}, overdraft_amount = #{overdraftAmount}, overdrawn_amount = #{overdrawnAmount} where id = #{walletId}")
    void updateWallet(Long walletId,VirtualWallet wallet);
}
