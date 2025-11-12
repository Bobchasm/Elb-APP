package com.tju.elm_bk.rich.mapper;

import com.tju.elm_bk.rich.domain.web.vo.WalletVO;
import com.tju.elm_bk.rich.entity.VirtualWallet;
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
            vr.name as vipName, vr.description as vipDescription,vr.overdraft_limit
        from virtual_wallet vm
        left join users u on vm.user_id = u.id
        left join virtual_wallet_vip_rule vr on vm.vip_level = vr.id
        where vm.user_id = #{userId}
    """)
    WalletVO queryWallet(Long userId);

    @Select("select * from virtual_wallet where user_id = #{userId} and is_deleted = 0")
    VirtualWallet getWalletById(Long userId);

    @Update("update virtual_wallet set status = #{status}, vip_level = #{vipLevel}, balance = #{balance}, overdraft_amount = #{overdraftAmount}, overdrawn_amount = #{overdrawnAmount} where id = #{id}")
    void updateWallet(VirtualWallet wallet);
}
