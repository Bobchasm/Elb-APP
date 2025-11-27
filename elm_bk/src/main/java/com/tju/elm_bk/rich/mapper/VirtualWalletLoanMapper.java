package com.tju.elm_bk.rich.mapper;

import com.tju.elm_bk.rich.domain.web.vo.LoanVO;
import com.tju.elm_bk.rich.entity.VirtualWalletLoan;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface VirtualWalletLoanMapper {

    @Insert("insert into virtual_wallet_loan (wallet_id,loan_amount,rate) values (#{walletId},#{amount},#{rate})")
    void load(Long walletId, BigDecimal amount,Float rate);

    @Update("update virtual_wallet_loan set repay_time = #{time} where id = #{id}")
    void repay(Long id, LocalDateTime time);

    @Select("select * from virtual_wallet_loan where wallet_id = #{walletId} order by create_time desc")
    List<VirtualWalletLoan> getLoanList(Long walletId);

    @Select("select * from virtual_wallet_loan where id = #{id}")
    VirtualWalletLoan getLoan(Long id);

}
