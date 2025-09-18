package com.tju.elm_bk.service.impl;


import com.tju.elm_bk.dto.PersonUpdateDTO;
import com.tju.elm_bk.entity.Person;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.PersonMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.service.PersonService;
import com.tju.elm_bk.utils.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PersonServiceImpl implements PersonService {
    @Autowired
    private PersonMapper personMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public Person getPersonByUserId(Long id) {
        return personMapper.getPersonByUserId(id);
    }

    @Override
    public void addPerson(Person person) {
        personMapper.insert(person);
    }

    @Override
    public Person updatePerson(PersonUpdateDTO updateDTO) {
        String currentUsername = SecurityUtils.getCurrentUsername()
                .orElseThrow(() -> new APIException("当前用户未登录"));
        User currentUser = userMapper.findByUsernameWithAuthorities(currentUsername);
        if (currentUser == null) {
            throw new APIException("当前用户不存在");
        }
        Long currentUserId = currentUser.getId();
        boolean isAdmin = currentUser.getAuthorities().stream()
                .anyMatch(auth -> "ADMIN".equals(auth.getName()));
        if (currentUserId==updateDTO.getId() || isAdmin){
            if (personMapper.getPersonByUserId(updateDTO.getId()) == null) {
                throw new APIException("该用户信息不存在！");
            }
            User user = new User();
            LocalDateTime now = LocalDateTime.now();
            user.setUpdateTime(now);
            user.setUpdater(currentUserId);
            user.setId(currentUserId);

            userMapper.update(user);
            Person p = new Person();
            BeanUtils.copyProperties(updateDTO,p);
            personMapper.updateById(p);
            Person person =personMapper.getPersonByUserId(updateDTO.getId());
            return person;
        }else{
            throw new APIException("当前用户无权限修改该用户信息！");
        }
    }
}
