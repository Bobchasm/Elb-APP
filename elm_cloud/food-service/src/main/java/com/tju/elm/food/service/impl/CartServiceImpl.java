package com.tju.elm.food.service.impl;

import com.tju.elm.api.client.BusinessClient;
import com.tju.elm.api.client.UserClient;
import com.tju.elm.api.po.Business;
import com.tju.elm.api.po.User;
import com.tju.elm.food.mapper.CartMapper;
import com.tju.elm.food.mapper.FoodMapper;
import com.tju.elm.food.pojo.entity.Cart;
import com.tju.elm.food.pojo.entity.Food;
import com.tju.elm.food.pojo.vo.CartItemVO;
import com.tju.elm.food.pojo.vo.CartVO;
import com.tju.elm.food.service.CartService;
import exception.APIException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import result.ResultCodeEnum;
import utils.UserContext;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class CartServiceImpl implements CartService {
    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private FoodMapper foodMapper;
    
    @Autowired
    private UserClient userClient;
    @Autowired
    private BusinessClient businessClient;


    @Override
    public List<CartItemVO> getCartItemList(Long businessId) {
        Long userId = getCurrentUser().getId();
        List<CartItemVO> ret = cartMapper.selectCartItems(userId, businessId);
        String businessName = businessClient.gainBusinessById(businessId).getData().getBusinessName();

        for (CartItemVO cartItemVO : ret) {
            cartItemVO.setBusinessName(businessName);
        }

        return ret;
    }

    @Override
    public Long addItem(Long foodId, Integer quantity) {
        Food food = foodMapper.selectFoodById(foodId);
        if (food == null) {
            throw new APIException(ResultCodeEnum.FOOD_MISSED);
        }
        if (food.getShelveStatus() != 1) {
            throw new APIException(ResultCodeEnum.FOOD_UNSHELVED);
        }
        Business business = businessClient.gainBusinessById(food.getBusinessId()).getData();
        if (business == null) {
            throw new APIException(ResultCodeEnum.BUSINESS_MISSED);
        }

        if (quantity <= 0) {
            throw new APIException(ResultCodeEnum.QUANTITY_ILLEGAL);
        }

        Long userId = getCurrentUser().getId();
        Cart cart = new Cart();

        cart.setCustomerId(userId);
        cart.setFoodId(foodId);
        cart.setQuantity(quantity);
        cart.setBusinessId(business.getId());

        cart.setCreator(userId);
        cart.setUpdater(userId);
        cart.setCreateTime(LocalDateTime.now());
        cart.setUpdateTime(LocalDateTime.now());
        cart.setIsDeleted(false);

        cartMapper.insertCart(cart);

        return cart.getId();
    }

    @Override
    public Long updateItem(Long cartId, Integer quantity) {
        Long userId = getCurrentUser().getId();

        Cart cart = cartMapper.selectCartById(cartId);
        if (cart == null) {
            return cartId;
        }
        if (!Objects.equals(cart.getCustomerId(), userId)) {
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        if (quantity < 0) {
            throw new APIException(ResultCodeEnum.QUANTITY_ILLEGAL);
        }
        if (quantity == 0) {
            cartMapper.removeCartItem(cartId);
            return cartId;
        }

        cartMapper.updateCartItem(cartId, quantity);
        return cart.getId();
    }

    @Override
    public Long clearCart(Long businessId) {
        Long userId = getCurrentUser().getId();
        cartMapper.clearCart(userId, businessId);
        return businessId;
    }

    @Override
    public Long removeItem(Long cartId) {
        Long userId = getCurrentUser().getId();
        Cart cart = cartMapper.selectCartById(cartId);
        if (!Objects.equals(cart.getCustomerId(), userId)) {
            throw new APIException(ResultCodeEnum.USER_DENIED);
        }
        cartMapper.removeCartItem(cartId);
        return cartId;
    }

    @Override
    public Cart getCartById(Long cartId) {
        return cartMapper.selectCartById(cartId);
    }

    /**
     * 获取当前用户ID
     */
    private User getCurrentUser() {
        return userClient.getUserByName(UserContext.getUsername()).getData();
    }
}
