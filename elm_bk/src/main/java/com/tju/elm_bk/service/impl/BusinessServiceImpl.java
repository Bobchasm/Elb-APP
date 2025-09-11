package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.entity.LoginUser;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.result.Result;
import com.tju.elm_bk.service.BusinessService;
import com.tju.elm_bk.entity.Business;
import com.tju.elm_bk.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class BusinessServiceImpl implements BusinessService {
    @Autowired
    private BusinessMapper businessMapper;  // 通过Spring注入Mapper实例

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public List<Business> listBusinessByOrderTypeId(Integer type) {
        List<Business> businessList= businessMapper.listBusinessByOrderTypeId(type);  // 通过实例调用方法
        return businessList;
    }

    @Override
    public Business getBusinessById(Business business) {
        if(null!=business)
            return businessMapper.getBusinessById(business.getBusinessId());
        return null;
    }

    @Override
    public int updateBusiness(Business business) {
        businessMapper.updateBusiness(business);
        Business b=businessMapper.getBusinessById(business.getBusinessId());
        if(b==null)
            businessMapper.saveBusinessMsg(business);
        else
            businessMapper.updateBusiness(business);
        return businessMapper.updateBusinessUser(business);
    }

    @Override
    public Business getBusinessByIdByPass(Business business) {
        return businessMapper.getBusinessByIdByPass(business);
    }

    @Override
    public List<Business> listBusinessByBusinessName(String businessName) {
        return businessMapper.listBusinessByBusinessName(businessName);
    }

    @Override
    public Result saveBusiness(Business business){
        if(businessMapper.checkBusiness(business)!=0){
            return Result.fail("用户已存在");
        }
        // 加密密码
        String encodedPassword = passwordEncoder.encode(business.getPassword());
        business.setPassword(encodedPassword);
        businessMapper.saveBusiness(business);
        return new Result(0, "注册成功");
    }

    @Override
    public int checkBusiness(Business business) {
        return businessMapper.checkBusiness(business);
    }

    @Override
    public int getBusinessIdByPhoneNumber(Business business){
        return businessMapper.getBusinessIdByPhoneNumber(business);
    }

    @Override
    public Result logout() {
        //获取SecurityContextHolder中的用户id
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        String userId = loginUser.getUsername();
        //删除redis中的用户信息
        String redisKey = "login:business:" + userId;
        Boolean deleteResult = redisTemplate.delete(redisKey);

        if (Boolean.TRUE.equals(deleteResult)) {
            return new Result(0, "退出成功");
        } else {
            return Result.fail("退出登录失败，未找到用户缓存");
        }
    }
}