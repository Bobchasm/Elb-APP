package com.tju.elm_bk.wallet.mapper;

import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm_bk.wallet.domain.web.vo.TransactionRecordVO;
import com.tju.elm_bk.wallet.entity.VirtualWalletTransaction;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface VirtualWalletTransactionMapper {
    @Select("""
        <script>
            select vmt.id, vmt.type, vmt.amount, vmt.fee,vmt.create_time
            from virtual_wallet_transaction vmt
            <where>
                vmt.is_deleted = 0
                and (vmt.from_account = #{walletId} or vmt.to_account = #{walletId})
                <if test="type!=null">
                    and vmt.type = #{type}
                </if>
                <if test="status!=null">
                    and vmt.status = #{status}
                </if>
                <if test="startTime!=null and endTime!=null">
                    and vmt.create_time &gt;= #{startTime} and vmt.create_time &lt;= #{endTime}
                </if>
            </where>
        </script>
    """)
    List<TransactionRecordVO> queryTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate);

    @Select("""
        select vmt.*, uf.username as fromAccountName, ut.username as toAccountName
        from virtual_wallet_transaction vmt
        left join virtual_wallet vf on vmt.from_account = vf.id
        left join virtual_wallet vt on vmt.to_account = vt.id
        left join users uf on vf.user_id = uf.id
        left join users ut on vt.user_id = ut.id
        where vmt.id = #{transactionId} and vmt.is_deleted = 0
    """)
    TransactionRecordDetailVO queryTransactionRecordDetail(Long transactionId);

    @Select("select * from virtual_wallet_transaction where order_id = #{orderId} and is_deleted = 0")
    VirtualWalletTransaction getTransactionByOrder(Long orderId);

    @Select("""
        select vmt.*, uf.username as fromAccountName, ut.username as toAccountName
        from virtual_wallet_transaction vmt
        left join virtual_wallet vf on vmt.from_account = vf.id
        left join virtual_wallet vt on vmt.to_account = vt.id
        left join users uf on vf.user_id = uf.id
        left join users ut on vt.user_id = ut.id
        where vmt.order_id = #{orderId} and vmt.is_deleted = 0
    """)
    TransactionRecordDetailVO queryTransactionByOrder(Long orderId);

    @Insert("""
        insert into virtual_wallet_transaction (type, status, amount, from_account, to_account, fee, fee_rate) 
        values (#{type}, #{status}, #{amount}, #{fromAccount}, #{toAccount}, #{fee}, #{feeRate})
    """)
    void createTransaction(VirtualWalletTransaction transaction);

    @Update("update virtual_wallet_transaction set status = #{status} where id = #{transactionId}")
    void thawTransaction(Long transactionId, Integer status);
}
