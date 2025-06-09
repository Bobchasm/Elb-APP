package com.tju.elm_bk.untity;


 // Lombok 注解（确保你的项目有 Lombok 依赖）
public class CreateOrderRequest {
    private String userId;
    private Integer businessId;
    private Integer daId;
    private Double orderTotal;

    public CreateOrderRequest(String userId, Integer businessId, Double orderTotal) {
        this.userId = userId;
    }
    public CreateOrderRequest() {
        super();
    }
    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;

    }

    public Integer getBusinessId() {
        return businessId;
    }
    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }
     public Integer getDaId(){
         return daId;
     }
     public void setDaId(Integer daId){
         this.daId = daId;
     }
    public Double getOrderTotal() {
       return orderTotal;
    }
    public void setOrderTotal(Double orderTotal) {
        this.orderTotal = orderTotal;
    }
}