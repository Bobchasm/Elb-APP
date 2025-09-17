package com.tju.elm_bk.mapper;

import com.tju.elm_bk.entity.Person;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PersonMapper {
    void insert(Person person);
}

