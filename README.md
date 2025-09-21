# 饿了么前后端分离项目

这是一个基于Spring Boot后端和Vue前端的饿了么外卖平台实现。项目包含完整的用户管理、商家管理、食品管理、购物车、订单处理等核心功能。

## 项目结构

- **elm_bk**: Spring Boot后端项目
  - 控制器(Controller)处理HTTP请求
  - 服务(Service)实现业务逻辑
  - 数据访问(Mapper)与数据库交互
  - 实体(Entity)对应数据库表
  - DTO用于数据传输
  - VO用于视图展示
  - 安全模块使用JWT认证

- **elmclient**: Vue前端项目
  - 使用Vue 2框架
  - 包含页面组件和公共组件
  - 路由配置和全局状态管理
  - 静态资源和API请求工具

## 主要功能

### 用户管理
- 用户注册与登录
- 个人信息管理
- 收货地址管理
- 密码修改

### 商家管理
- 商家信息维护
- 根据分类查询商家
- 查看商家详情

### 食品管理
- 查询商家食品列表
- 食品信息展示

### 购物车管理
- 添加商品到购物车
- 修改购物车商品数量
- 从购物车移除商品
- 查看购物车详情

### 订单管理
- 创建新订单
- 查看订单详情
- 查询用户订单列表
- 根据商家和状态查询订单
- 设置订单状态

## 技术栈

**后端:**
- Spring Boot 2.x
- MyBatis Plus
- JWT认证
- Swagger API文档
- MySQL数据库

**前端:**
- Vue.js 2.x
- Vue Router
- Axios
- Element UI组件库

## 安装与运行

**后端:**
1. 安装JDK 1.8+ 和 Maven
2. 导入数据库文件 `elm_bk.sql`
3. 修改 `application.yml` 中的数据库连接信息
4. 使用Maven构建项目: `mvn clean package`
5. 运行Spring Boot应用: `java -jar elm_bk.jar`

**前端:**
1. 安装Node.js和npm
2. 进入elmclient目录: `cd elmclient`
3. 安装依赖: `npm install`
4. 运行开发服务器: `npm run serve`
5. 构建生产环境版本: `npm run build`

## API文档

完整的API文档可通过Swagger访问: `http://localhost:8080/swagger-ui.html`

## 数据库设计

包含以下主要表:
- 用户表(users)
- 商家表(businesses)
- 食品表(foods)
- 购物车表(carts)
- 订单表(orders)
- 收货地址表(addresses)

## 许可证

本项目采用MIT许可证。详见 LICENSE 文件。