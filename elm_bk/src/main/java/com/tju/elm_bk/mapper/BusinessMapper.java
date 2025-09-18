package com.tju.elm_bk.mapper;
import java.util.List;

import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.vo.BusinessVO;
import org.apache.ibatis.annotations.*;


@Mapper
public interface BusinessMapper {
    @Select("SELECT b.* FROM business b WHERE b.id = #{businessId)}")
    BusinessVO selectBusinessVO(Long businessId);
}
