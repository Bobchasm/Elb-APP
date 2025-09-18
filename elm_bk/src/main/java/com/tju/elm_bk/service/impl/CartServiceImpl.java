package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.CartMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.vo.CartVO;
import com.tju.elm_bk.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BusinessMapper businessMapper;

    @Override
    public CartVO addCart(CartItemCreateDTO cartItemCreateDTO) {
        if (!cartItemCreateDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }
        Cart cart = new Cart();
        BeanUtils.copyProperties(cartItemCreateDTO, cart);
        cart.setBusinessId(cartItemCreateDTO.getBusiness() == null ? null : cartItemCreateDTO.getBusiness().getId());
        cart.setFoodId(cartItemCreateDTO.getFood() == null ? null : cartItemCreateDTO.getFood().getId());
        cart.setCustomerId(cartItemCreateDTO.getCustomer() == null ? null : cartItemCreateDTO.getCustomer().getId());
        cart.setCreateTime(LocalDateTime.now());
        cart.setIsDeleted(false);

        cartMapper.insertCart(cart);
        CartVO cartVO = cartMapper.selectCart(cart.getId());
        User customer = userMapper.findByUsernameWithAuthorities(userMapper.findById(cart.getCustomerId()).getUsername());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(customer,userVO);
        cartVO.setCustomer(userVO);
        cartVO.setBusiness(businessMapper.selectBusinessVO(cartVO.getBusiness().getId()));

        return null;
    }
}
