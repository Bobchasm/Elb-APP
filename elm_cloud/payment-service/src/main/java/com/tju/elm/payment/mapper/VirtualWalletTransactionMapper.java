package com.tju.elm.payment.mapper;

import com.tju.elm.payment.domain.web.vo.TransactionRecordDetailVO;
import com.tju.elm.payment.domain.web.vo.TransactionRecordVO;
import com.tju.elm.payment.entity.VirtualWalletTransaction;
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
            select vmt.id,
                CASE
                    -- 如果是付款方（from_account），保持原始类型
                    WHEN vmt.from_account = #{walletId} THEN vmt.type
                    -- 如果是收款方（to_account），需要调整类型
                    WHEN vmt.to_account = #{walletId} THEN
                        CASE
                            -- 原始类型是支付(0)，对于收款方应该显示为收款(1)
                            WHEN vmt.type = 0 THEN 1
                            -- 原始类型是收款(1)，对于付款方应该显示为支付(0)
                            WHEN vmt.type = 1 THEN 0
                            -- 其他类型（提现、充值、退款）保持不变
                            ELSE vmt.type
                        END
                    ELSE vmt.type
                END as type,
                vmt.amount, vmt.fee, vmt.create_time,
                CASE
                    WHEN vmt.from_account = #{walletId} THEN 0
                    ELSE 1
                END as inOrOut
            from virtual_wallet_transaction vmt
            <where>
                vmt.is_deleted = 0
                and (vmt.from_account = #{walletId} or vmt.to_account = #{walletId})
                <if test="type!=null">
                    -- 根据查询类型过滤，需要考虑双向转换
                    and (
                        (vmt.from_account = #{walletId} and vmt.type = #{type}) or
                        (vmt.to_account = #{walletId} and (
                            (#{type} = 0 and vmt.type = 1) or
                            (#{type} = 1 and vmt.type = 0) or
                            (#{type} != 0 and #{type} != 1 and vmt.type = #{type})
                        ))
                    )
                </if>
                <if test="status!=null">
                    and vmt.status = #{status}
                </if>
                <if test="startDate!=null and endDate!=null">
                    and vmt.create_time &gt;= #{startDate} and vmt.create_time &lt;= #{endDate}
                </if>
            </where>
            order by vmt.create_time desc
        </script>
    """)
    List<TransactionRecordVO> queryTransactionRecord(Long walletId, Integer type, Integer status, LocalDate startDate, LocalDate endDate);

    @Select("""
        select vmt.*
        from virtual_wallet_transaction vmt
        left join virtual_wallet vf on vmt.from_account = vf.id
        left join virtual_wallet vt on vmt.to_account = vt.id
        where vmt.id = #{transactionId} and vmt.is_deleted = 0
    """)
    TransactionRecordDetailVO queryTransactionRecordDetail(Long transactionId);

    @Select("select * from virtual_wallet_transaction where order_id = #{orderId} and is_deleted = 0")
    VirtualWalletTransaction getTransactionByOrder(Long orderId);

    @Select("""
        select vmt.*
        from virtual_wallet_transaction vmt
        left join virtual_wallet vf on vmt.from_account = vf.id
        left join virtual_wallet vt on vmt.to_account = vt.id
        where vmt.order_id = #{orderId} and vmt.is_deleted = 0
    """)
    TransactionRecordDetailVO queryTransactionByOrder(Long orderId);

    @Insert("""
        insert into virtual_wallet_transaction (type, status, amount, from_account, to_account, fee, fee_rate, order_id, create_time) 
        values (#{type}, #{status}, #{amount}, #{fromAccount}, #{toAccount}, #{fee}, #{feeRate}, #{orderId}, #{createTime})
    """)
    void createTransaction(VirtualWalletTransaction transaction);

    @Update("update virtual_wallet_transaction set status = #{status} where id = #{transactionId}")
    void thawTransaction(Long transactionId, Integer status);
}
