package com.neusoft.elm.po;

public class Cart {
    private Integer cartId;
    private Integer foodId;
    private Integer businessId;
    private String userId;
    private Integer quantity;

    //多对一：所属食品
    private Food food;
    //多对一：所属商家
    private Business business;

    //get、set ... ...
}
