### 资源统计

- 腾讯云
  
  ip：REDACTED_IP
  
  username：chasm
  
  passward：chasm123

- 阿里云A
  
  - ip：REDACTED_IP
  
  - username：bob
  
  - password：bob123

- 阿里云B
  
  - ip ：REDACTED_IP
  
  - username：chenyuze
  
  - password：chenyuze

- 阿里云C
  
  - ip ：REDACTED_IP
  
  - username：lijiali
  
  - password：lijiali77

- 阿里云D
  
  - ip：REDACTED_IP
  
  - username：zy
  
  - password：123

### 拆分

- user-service (8888)
  
  用户管理+成为商家权限+地址+其他(系统配置查询)

- order-service (8885)
  
  所有订单相关

- business-service (8882)
  
  所有店铺相关

- food-service (8883)
  
  所有商品相关+购物车

- point-service (8887)
  
  积分系统

- payment-service (8886)
  
  虚拟钱包

- notification-service (8884)
  
  消息+websocket+文件上传

- ai-service (8881)
  
  ai相关

路由：

- elm-gateway (8086)

### 部署计划

| 服务器          | 主要用途                  | 部署内容                                                                                 |
| ------------ | --------------------- | ------------------------------------------------------------------------------------ |
| **腾讯云**      | 前端+注册中心+网关+数据库主节点     | `nginx` +`frontend`+`nacos`+`gateway`+`mysql主节点`+`redis主节点`                          |
| **阿里云ECS A** | 服务集群                  | `payment-service`+`point-service`+`user-service-instance1`+`order-service-instance2` |
| **阿里云ECS B** | 服务集群                  | `order-service-instance1`+`food-service1`+`business-service1`+`notification-service` |
| **阿里云ECS C** | 服务集群                  | `user-service-instance2`+`business-service2`+`food-service2`+`ai-service`            |
| **阿里云ECS D** | 数据库从节点+中间件+其他(服务治理那些) | `mysql从节点`+`redis从节点`+`rocketmq`                                                     |

现在情况：

- 使用的是rabbitmq而非rocketmq，且rabbitmq暂时部署在腾讯云上

- mysql和redis现在均使用腾讯云上的

- 其他均与计划一致

### 部署信息

| 服务                   | 端口   | 实例数 | 容器名                  |
| -------------------- | ---- | --- | -------------------- |
| 前端                   | 80   | 1   | elm-micro-fr         |
| gateway              | 8086 | 1   | elm-gateway-app      |
| user-service         | 8888 | 2   | elm-user-app         |
| order-service        | 8885 | 2   | elm-order-app        |
| business-service     | 8882 | 2   | elm-business-app     |
| food-service         | 8883 | 2   | elm-food-app         |
| point-service        | 8887 | 1   | elm-point-app        |
| payment-service      | 8886 | 1   | elm-payment-app      |
| notification-service | 8884 | 1   | elm-notification-app |
| ai-service           | 8881 | 1   | elm-ai-app           |

注意：各个镜像的压缩包放了一份到服务器的 `/docker_images` 下，服务的dockerfile放在项目对应模块下

nacos：[http://REDACTED_DOMAIN:8848/nacos](http://REDACTED_DOMAIN:8848/nacos)

### 部署步骤

每台服务器的目录都一样，镜像、容器名称都比较统一

**后端统一步骤**

1.先打jar包，每个服务分别打，jar包在如下图左侧选中的地方

注意：如果修改了 `common`和 `api`模块，需要重新 `install` 这两个模块(也是右侧maven生命周期那里)，然后再打服务的jar包，否则可能会有问题

![](G:\LE\elm-microservices\gra\packge.png)

2.进入服务专用目录

```bash
# ssh连接服务器
# 进入项目专门目录
cd elm-micro

# 进入对应服务的目录(网关跳过这一步)，如订单
cd order-service
```

![](G:\LE\elm-microservices\gra\server1.png)

3.在该目录删除原来的jar文件，然后上传该服务打好的jar包，并改名为 `elm-<服务名>-app.jar`，(反正服务器上原有的是什么名就改成什么名)

如订单服务jar包重命名为 `elm-order-app.jar`

4.重启服务

```bash
# 重启
docker restart <服务容器名>
# 如：
docker restart elm-order-app
# 网关是：
docker restart elm-gateway-app

# 查看日志看是否成功
docker logs -f <服务容器名>
docker logs -f <服务容器名> --tail 200 # 只显示后200行
```

注意：如果修改了yml配置文件也需要重启，服务器上的配置文件与本地的有所不同

---

**前端**

github上的前端请求后端路径还是localhost:8086，这是为了便于本地调试看后端控制台的报错(这时要启动所有服务和网关的本地后端)；如果测部署上的或者要重新部署前端把`localhost`改成`REDACTED_DOMAIN`

1.本地打包，成功后出现目录`dist`，将其压缩为 `.tar`

![](G:\LE\elm-microservices\gra\front-server.png)

2.上传 `dist.tar` 至服务器

```bash
# 路径在腾讯云的 /elm-micro 下
cd elm-micro

# 上传
# 解压缩
tar -xvf dist.tar
```

3.重启前端容器

```bash
docker restart elm-micro-fr
```

### 描述

**已经干的以及相较于原来的小变化**

- 拆分，服务间调用，也就是整个项目现在是可以正常运行的，就像原来的单体架构一样

- OSS已经改成腾讯的COS了

- websocket整个放在`notification-service`里，提供实时消息的接口，就不用每个服务单独一个websocket的服务器了

- 弱化了springboot-security的使用，基本上只在网关鉴权用，所以不能使用注解来拦截相关权限(实则是懒得研究了)
  
  特别说明后端怎么获取当前用户信息，大部分service层已经写好类似的直接调用就行
  
  ```java
  // 注入
  @Autowired
  private UserClient userClient;
  
  // 在要用的类中写一个：
  private User getCurrentUser() {
      return userClient.getUserByName(UserContext.getUsername()).getData();
  }
  ```
  
  `UserContext.getUsername()` 是获取当线程保存的用户名，然`userClient`是调用`user-service`的接口。线程保存的用户名来自在common模块里的通用过滤器

- 重点注意一下依赖管理，有些公共依赖我在整个父工程的pom下进行了版本管理，并在common的pom里引入了。然后common下的pom已经引入了动态数据库的依赖，但是设成optional了，别的服务还得再引，如果不用这个依赖，直接删掉就好

**可能还需要干的**

1. 配置管理，共享的搞到nacos上去

2. 服务治理（服务熔断也没做现在）

3. 分布式缓存和数据库，现在只有单数据库和缓存在腾讯云的服务器

4. 现在用的是rabbitmq，考虑改用rocketmq(老师文档写的rocketmq)，现在rabbitmq主要用在下单、订单状态等会增加积分的操作上，其他地方还没用

5. 给下单操作加上高并发支持，缓存+消息队列异步+lua脚本原子操作

6. 给更多的接口加上缓存支持

4、5、6可以后面我来，然后分布式缓存可以先构建一个框架和方案，因为现在没几个接口加了缓存


