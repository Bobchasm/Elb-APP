package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.Person;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PersonMapper {
    void insert(Person person);

    @Select("select * from person where id = #{id}")
    Person getPersonByUserId(Long id);

    void updateById(Person updateDTO);
}

