# 饿了么后端 API 接口文档

## 基础信息

- 服务器地址: `http://localhost:8080`
- 所有接口都支持跨域访问

## 用户管理 (UserController)

### 1. 用户登录验证

- **接口地址**: `GET /UserController/getUserByIdByPass`
- **功能**: 根据用户编号和密码查询用户信息
- **请求参数**:
  - `userId` (String): 用户编号
  - `password` (String): 密码
- **返回值**: User 对象
- **示例**:
  ```
  GET /UserController/getUserByIdByPass?userId=user123&password=123456
  ```

### 2. 检查用户是否存在

- **接口地址**: `GET /UserController/getUserById`
- **功能**: 根据用户编号查询用户是否存在
- **请求参数**:
  - `userId` (String): 用户编号
- **返回值**: Integer (返回行数，0 表示不存在，大于 0 表示存在)
- **示例**:
  ```
  GET /UserController/getUserById?userId=user123
  ```

### 3. 用户注册

- **接口地址**: `POST /UserController/saveUser`
- **功能**: 注册新用户
- **请求参数**: User 对象（JSON 格式）
  - `userId` (String): 用户编号
  - `password` (String): 密码
  - `userName` (String): 用户名
  - `userSex` (Integer): 性别
- **返回值**: Integer (影响的行数)
- **示例**:
  ```json
  POST /UserController/saveUser
  {
    "userId": "user123",
    "password": "123456",
    "userName": "张三",
    "userSex": 1
  }
  ```

## 商家管理 (BusinessController)

### 1. 根据分类查询商家列表

- **接口地址**: `GET /BusinessController/listBusinessByOrderTypeId`
- **功能**: 根据点餐分类编号查询商家信息
- **请求参数**:
  - `orderTypeId` (Integer): 分类编号
- **返回值**: Business 数组
- **示例**:
  ```
  GET /BusinessController/listBusinessByOrderTypeId?orderTypeId=1
  ```

### 2. 根据 ID 查询商家详情

- **接口地址**: `GET /BusinessController/getBusinessById`
- **功能**: 根据商家编号查询商家详细信息
- **请求参数**:
  - `businessId` (Integer): 商家编号
- **返回值**: Business 对象
- **示例**:
  ```
  GET /BusinessController/getBusinessById?businessId=1
  ```

## 食品管理 (FoodController)

### 1. 查询商家食品列表

- **接口地址**: `GET /FoodController/listFoodByBusinessId`
- **功能**: 根据商家编号查询该商家的所有食品
- **请求参数**:
  - `businessId` (Integer): 商家编号
- **返回值**: Food 数组
- **示例**:
  ```
  GET /FoodController/listFoodByBusinessId?businessId=1
  ```

## 购物车管理 (CartController)

### 1. 查询购物车

- **接口地址**: `GET /CartController/listCart`
- **功能**: 查询用户购物车信息
- **请求参数**:
  - `userId` (String): 用户编号
  - `businessId` (Integer, 可选): 商家编号，为空时查询所有商家
- **返回值**: Cart 数组
- **示例**:
  ```
  GET /CartController/listCart?userId=user123&businessId=1
  ```

### 2. 添加到购物车

- **接口地址**: `POST /CartController/saveCart`
- **功能**: 将食品添加到购物车
- **请求参数**:
  - `userId` (String): 用户编号
  - `businessId` (Integer): 商家编号
  - `foodId` (Integer): 食品编号
- **返回值**: Integer (影响的行数)
- **示例**:
  ```
  POST /CartController/saveCart?userId=user123&businessId=1&foodId=1
  ```

### 3. 更新购物车数量

- **接口地址**: `POST /CartController/updateCart`
- **功能**: 更新购物车中食品的数量
- **请求参数**:
  - `userId` (String): 用户编号
  - `businessId` (Integer): 商家编号
  - `foodId` (Integer): 食品编号
  - `quantity` (Integer): 数量
- **返回值**: Integer (影响的行数)
- **示例**:
  ```
  POST /CartController/updateCart?userId=user123&businessId=1&foodId=1&quantity=2
  ```

### 4. 从购物车移除

- **接口地址**: `POST /CartController/removeCart`
- **功能**: 从购物车中移除食品
- **请求参数**:
  - `userId` (String): 用户编号
  - `businessId` (Integer): 商家编号
  - `foodId` (Integer, 可选): 食品编号，为空时移除该商家所有食品
- **返回值**: Integer (影响的行数)
- **示例**:
  ```
  POST /CartController/removeCart?userId=user123&businessId=1&foodId=1
  ```

## 收货地址管理 (DeliveryAddressController)

### 1. 查询用户收货地址列表

- **接口地址**: `GET /DeliveryAddressController/listDeliveryAddressByUserId`
- **功能**: 查询用户的所有收货地址
- **请求参数**:
  - `userId` (String): 用户编号
- **返回值**: DeliveryAddress 数组
- **示例**:
  ```
  GET /DeliveryAddressController/listDeliveryAddressByUserId?userId=user123
  ```

### 2. 根据 ID 查询收货地址

- **接口地址**: `GET /DeliveryAddressController/getDeliveryAddressById`
- **功能**: 根据地址编号查询收货地址详情
- **请求参数**:
  - `daId` (Integer): 地址编号
- **返回值**: DeliveryAddress 对象
- **示例**:
  ```
  GET /DeliveryAddressController/getDeliveryAddressById?daId=1
  ```

### 3. 添加收货地址

- **接口地址**: `POST /DeliveryAddressController/saveDeliveryAddress`
- **功能**: 添加新的收货地址
- **请求参数**:
  - `contactName` (String): 联系人姓名
  - `contactSex` (Integer): 联系人性别
  - `contactTel` (String): 联系人电话
  - `address` (String): 详细地址
  - `userId` (String): 用户编号
- **返回值**: Integer (影响的行数)
- **示例**:
  ```
  POST /DeliveryAddressController/saveDeliveryAddress?contactName=张三&contactSex=1&contactTel=13888888888&address=北京市朝阳区&userId=user123
  ```

### 4. 更新收货地址

- **接口地址**: `POST /DeliveryAddressController/updateDeliveryAddress`
- **功能**: 更新收货地址信息
- **请求参数**: DeliveryAddress 对象（JSON 格式）
- **返回值**: Integer (影响的行数)
- **示例**:
  ```json
  POST /DeliveryAddressController/updateDeliveryAddress
  {
    "daId": 1,
    "contactName": "李四",
    "contactSex": 1,
    "contactTel": "13999999999",
    "address": "上海市浦东新区",
    "userId": "user123"
  }
  ```

### 5. 删除收货地址

- **接口地址**: `POST /DeliveryAddressController/removeDeliveryAddress`
- **功能**: 删除收货地址
- **请求参数**:
  - `daId` (Integer): 地址编号
- **返回值**: Integer (影响的行数)
- **示例**:
  ```
  POST /DeliveryAddressController/removeDeliveryAddress?daId=1
  ```

## 订单管理 (OrdersController)

### 1. 创建订单

- **接口地址**: `POST /OrdersController/createOrders`
- **功能**: 创建新订单，自动从购物车生成订单明细并清空购物车
- **请求参数**: CreateOrderRequest 对象（JSON 格式）
  - `userId` (String): 用户编号
  - `businessId` (Integer): 商家编号
  - `daId` (Integer): 收货地址编号
  - `orderTotal` (Double): 订单总金额
- **返回值**: Integer (订单编号)
- **示例**:
  ```json
  POST /OrdersController/createOrders
  {
    "userId": "user123",
    "businessId": 1,
    "daId": 1,
    "orderTotal": 58.50
  }
  ```

### 2. 根据订单 ID 查询订单详情

- **接口地址**: `GET /OrdersController/getOrdersById`
- **功能**: 查询订单详细信息，包括商家信息和订单明细
- **请求参数**:
  - `orderId` (Integer): 订单编号
- **返回值**: Orders 对象（包含商家信息和订单明细）
- **示例**:
  ```
  GET /OrdersController/getOrdersById?orderId=1
  ```

### 3. 查询用户订单列表

- **接口地址**: `GET /OrdersController/listOrdersByUserId`
- **功能**: 查询用户的所有订单信息
- **请求参数**:
  - `userId` (String): 用户编号
- **返回值**: Orders 数组（包含商家信息和订单明细）
- **示例**:
  ```
  GET /OrdersController/listOrdersByUserId?userId=user123
  ```

## 数据模型说明

### User (用户)

```json
{
  "userId": "用户编号",
  "password": "密码",
  "userName": "用户名",
  "userSex": "性别(0:女 1:男)",
  "userImg": "头像",
  "delTag": "删除标记"
}
```

### Business (商家)

```json
{
  "businessId": "商家编号",
  "businessName": "商家名称",
  "businessAddress": "商家地址",
  "businessExplain": "商家介绍",
  "businessImg": "商家图片",
  "orderTypeId": "分类编号",
  "starPrice": "起送费",
  "deliveryPrice": "配送费",
  "remarks": "备注"
}
```

### Food (食品)

```json
{
  "foodId": "食品编号",
  "foodName": "食品名称",
  "foodExplain": "食品介绍",
  "foodImg": "食品图片",
  "foodPrice": "食品价格",
  "businessId": "所属商家编号",
  "remarks": "备注"
}
```

### Cart (购物车)

```json
{
  "cartId": "购物车编号",
  "foodId": "食品编号",
  "businessId": "商家编号",
  "userId": "用户编号",
  "quantity": "数量",
  "food": "食品对象",
  "business": "商家对象"
}
```

### DeliveryAddress (收货地址)

```json
{
  "daId": "地址编号",
  "contactName": "联系人姓名",
  "contactSex": "联系人性别(0:女 1:男)",
  "contactTel": "联系人电话",
  "address": "详细地址",
  "userId": "用户编号"
}
```

### Orders (订单)

```json
{
  "orderId": "订单编号",
  "userId": "用户编号",
  "businessId": "商家编号",
  "orderDate": "订单日期",
  "orderTotal": "订单总金额",
  "daId": "收货地址编号",
  "orderState": "订单状态(0:未支付 1:已支付)",
  "business": "商家对象",
  "list": "订单明细数组"
}
```

### OrderDetailet (订单明细)

```json
{
  "odId": "订单明细编号",
  "orderId": "订单编号",
  "foodId": "食品编号",
  "quantity": "数量",
  "food": "食品对象"
}
```

## 错误处理

- 所有接口在发生错误时会返回相应的 HTTP 状态码
- 数据库操作返回影响的行数，0 表示操作失败
- 查询类接口如果没有找到数据会返回 null 或空数组

## 注意事项

1. 所有 POST 请求的 Content-Type 应为 application/json（JSON 请求体）或 application/x-www-form-urlencoded（表单参数）
2. 订单状态：0 表示未支付，1 表示已支付
3. 性别字段：0 表示女，1 表示男
4. 创建订单时会自动清空对应商家的购物车数据
5. 所有金额字段使用 Double 类型，建议保留两位小数
