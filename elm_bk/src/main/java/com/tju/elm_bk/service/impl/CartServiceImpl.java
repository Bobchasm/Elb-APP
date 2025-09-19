package com.tju.elm_bk.service.impl;

import com.tju.elm_bk.dto.CartItemCreateDTO;
import com.tju.elm_bk.entity.Cart;
import com.tju.elm_bk.entity.Food;
import com.tju.elm_bk.entity.User;
import com.tju.elm_bk.exception.APIException;
import com.tju.elm_bk.mapper.BusinessMapper;
import com.tju.elm_bk.mapper.CartMapper;
import com.tju.elm_bk.mapper.FoodMapper;
import com.tju.elm_bk.mapper.UserMapper;
import com.tju.elm_bk.result.ResultCodeEnum;
import com.tju.elm_bk.service.CartService;
import com.tju.elm_bk.utils.SecurityUtils;
import com.tju.elm_bk.vo.CartItemVO;
import com.tju.elm_bk.vo.CartVO;
import com.tju.elm_bk.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private BusinessMapper businessMapper;
    @Autowired
    private FoodMapper foodMapper;

    @Override
    public CartVO addCart(CartItemCreateDTO cartItemCreateDTO) {
        if (!cartItemCreateDTO.verify()) {
            throw new APIException(ResultCodeEnum.PARAM_NOT_MATCHED);
        }

        User user = userMapper.findByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        Food food = foodMapper.selectFoodById(cartItemCreateDTO.getFood().getId());
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }

        Cart cart = new Cart();
        BeanUtils.copyProperties(cartItemCreateDTO, cart);
        cart.setBusinessId(cartItemCreateDTO.getBusiness().getId());
        cart.setFoodId(cartItemCreateDTO.getFood().getId());
        cart.setCustomerId(user.getId());
        cart.setCreator(user.getId());
        cart.setUpdater(user.getId());
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cart.setIsDeleted(false);

        cartMapper.insertCart(cart);
        CartVO cartVO = cartMapper.selectCart(cart.getId());
        User customer = userMapper.findByUsernameWithAuthorities(userMapper.findById(cart.getCustomerId()).getUsername());
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(customer,userVO);
        cartVO.setCustomer(userVO);
        cartVO.setBusiness(businessMapper.selectBusinessVO(cartVO.getBusinessId()));
        return cartVO;
    }





    @Override
    public List<CartItemVO> getCartItemList(Long businessId) {
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        return cartMapper.selectCartItems(userId, businessId);
    }

    @Override
    public Integer addItem(Long foodId, Integer quantity) {
        if (quantity < 0) {
            throw new APIException(ResultCodeEnum.QUANTITY_ILLEGAL);
        }
        if (quantity == 0) {
            return 0;
        }
        Long userId = userMapper.getUserIdByUsername(SecurityUtils.getCurrentUsername().orElseThrow(() -> new APIException(ResultCodeEnum.VALUE_MISSED)));
        cartMapper.insertCartItem(userId,foodId,quantity);

        return null;
    }

    @Override
    public Integer updateItem(Long cartId, Integer quantity) {
        return 0;
    }

    @Override
    public Integer clearCart(Long businessId) {
        return 0;
    }

    @Override
    public Integer removeItem(Long cartId) {
        return 0;
    }
}
