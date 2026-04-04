# 微服务整体描述

### 资源统计

- 腾讯云 (4c4g REDACTED_DOMAIN)
  
  ip：REDACTED_IP
  
  username：chasm
  
  passward：chasm123

  ps:已配置ssl证书，有些中间件可能需要使用nginx转发(用户chasm登录,`/ssl/REDACTED_DOMAIN.conf` 添加相应转发配置)

- 阿里云1 (2c2g)
  
  - ip：123.57.102.69

  - username：elm

  - password：@elm2026#

- 阿里云2 (2c4g)
  
  - ip ：47.93.197.249
  
  - username：elm
  
  - password：@elm2026#

- 阿里云3 (2c4g)
  
  - ip ：47.94.95.135
  
  - username：elm
  
  - password：@elm2026#

- 阿里云4 (2c2g)
  
  - ip：123.56.202.25
  
  - username：elm
  
  - password：@elm2026#

### 拆分(模块名)

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
  
  消息+websocket+文件上传+ai

路由：

- elm-gateway (8080)

### 部署计划

| 服务器          | 主要用途          | 部署内容                                                                                                       |
|--------------|---------------|------------------------------------------------------------------------------------------------------------|
| **腾讯云**      | 前端+一些常规中间件+路由 | `nginx`+`nacos`+`mysql主从`+`redis主从`+`rabbitmq`  +`gateway` + `elasticsearch`                               |
| **阿里云ECS 1** | 服务治理中间件       | `sentinel` + `elasticsearch-head` + `jaeger-agent` + `jaegertracing/all-in-one`                            |
| **阿里云ECS 2** | 服务集群          | `order-service1`+`food-service1`+`business-service1`+`user-service1`+`notification-service`+`payment-service` |
| **阿里云ECS 3** | 服务集群          | `order-service2`+`food-service2`+`business-service2`+`user-service2`+`point-service`                       |
| **阿里云ECS 4** | 其他中间件         |                                                                                                            |


### 部署信息

| 服务                  | 端口   | 实例数 | 容器名                                |
| ------------------- |------| --- |------------------------------------|
| 前端                  | 80   | 1   | nginx-proxy (除前端外,还负责部分别的中间件的反向代理) |
| gateway             | 8080 | 1   | elm-gateway-app                    |
| user-service        | 8888 | 2   | elm-user-app                       |
| order-service       | 8885 | 2   | elm-order-app                      |
| business-service    | 8882 | 2   | elm-business-app                   |
| food-service        | 8883 | 2   | elm-food-app                       |
| point-service       | 8887 | 1   | elm-point-app                      |
| payment-service     | 8886 | 1   | elm-payment-app                    |
| notification-service | 8884 | 1   | elm-notification-app               |

注意：各个镜像的压缩包放了一份到服务器的 `/docker_images` 下，服务的dockerfile放在项目对应模块下

**rabbitmq** <br>
  [http://REDACTED_DOMAIN:15672/](http://REDACTED_DOMAIN:15672/) <br>
  username: rabbit
  password: rabbit

**nacos** <br>
  [http://REDACTED_DOMAIN:8848/nacos](http://REDACTED_DOMAIN:8848/nacos) <br>
  username: nacos <br>
  password: nacos

**sentinel** <br>
  [http://123.57.102.69:8858/](http://123.57.102.69:8858/) <br>
  username: sentinel <br>
  password: sentinel

**jaeger** <br>
  [http://123.57.102.69:16686/](http://123.57.102.69:16686/) <br>

**elasticsearch-head (elasticsearch图形化界面)** <br>
  页面上方的connect的url是 [http://REDACTED_DOMAIN:9200/](http://REDACTED_DOMAIN:9200/) <br>
  [http://123.57.102.69:9100/](http://123.57.102.69:9100/)



### 服务部署步骤

每台服务器的目录都一样，镜像、容器名称都比较统一

**后端统一步骤**

1.先打jar包，每个服务分别打，jar包在如下图左侧选中的地方

注意：如果修改了 `common`和`api`模块，需要重新 `clean` + `install` 这两个模块，然后再打服务的jar包，否则可能会有问题

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

注意：如果修改了配置文件(nacos上非单个服务配置和部署时挂载的application.yml和bootstrap.yml)也需要重启，服务器上的配置文件与本地的有所不同

---

**前端**

注：本地测试时 `request.js` 里的请求路径以及仓库中的路径保持如下

```javascript
export const BASE_URL = 'http://localhost:8080';
export const WS_BASE_URL = 'ws://localhost';
```

如果重新部署前端，需要先修改成（端口不用是因为部署的服务器配置了nginx反向代理，前缀为`/api`的请求自动转发到`8080`端口）：

```javascript
export const BASE_URL = 'https://REDACTED_DOMAIN';
export const WS_BASE_URL = 'wss://REDACTED_DOMAIN';
```

打包好后push仓库前最好再改回去

1.本地打包，成功后出现目录`dist`，将其压缩为 `.tar`

![](G:\LE\elm-microservices\gra\front-server.png)

2.上传 `dist.tar` 至服务器

```bash
# 路径在腾讯云的 /elm 下
cd elm

# 把原来的dist压缩包和目录删掉然后上传
# 解压缩
tar -xvf dist.tar
```

3.重启前端容器

```bash
docker restart elm-vue-app
```

### 描述

**已经干的以及相较于原来的变化**

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

**微服务化特性汇总**

- 路由、注册中心、RPC ok
- 配置管理 ok
  后续服务治理那块如果要加配置的话可以上nacos配置中心看看有没有对应的配置文件，如果新加共享配置文件的话需要在每个服务的`bootstrap.yml`中的`shared-configs:`下添加配置文件名
- 动态路由
- 积分兑换高并发支持，缓存+消息队列异步+lua脚本原子操作 ok
- 适用接口加上缓存 redis+caffeine ok
- 主从数据库 ok
- 搜索引擎
- 服务治理 (熔断、降级、限流等) <br>
  先把jaeger在common下的配置类注释掉了，因为一直报错，jaeger相关的中间件先部署到aliyun1了，不知道能不能用，主要参考这篇 https://blog.csdn.net/dielian520/article/details/103076176?fromshare=blogdetail&sharetype=blogdetail&sharerId=103076176&sharerefer=PC&sharesource=2301_80080821&sharefrom=from_link
- 前端代码加密
- https ok


### 计划&与智慧树对比

#### 1 实现概述

Ribbon

- 负载均衡器

- 结合 Feign 实现服务间调用


网关 gateway

基于 Spring Cloud Bus 的配置刷新

基于 Spring Cloud Bus 的配置管理

Hystrix

- 服务熔断、降级、隔离、限流

- 延迟 容错


Eureka

- 实现服务注册发现

- 多个注册中心，实现一个高可用集群


gateway

- 动态路由

- 网关过滤器

- 网关熔断


配置中心

- springcloud config

  这个是基于git，在这里存配置文件

  bus动态刷新


#### 2 不同的实现

1. 注册中心

原：Eureka

我们：nacos

2. 配置管理

原：使用springcloud config(基于git)，基于 Spring Cloud Bus 的配置管理和刷新

我们：全部基于nacos

3. 服务间调用

原：feign

我们：OpenFeign

4. 服务治理系列

原：Hystrix

我们：sentinel

5. 缓存

原：提到了一个redis+Memcached分布式高速缓存系统

我们：redis+caffine


#### 3 可能改的东西

**智慧树提到的**

数据库主从结构

搜索引擎 Elasticsearch

缓存 看到课件上提到了一个组合redis+Memcached分布式高速缓存系统

动态路由 (架构的时候没调好就砍掉了)、网关熔断

注册中心高可用集群

**可能加的特色**

服务治理那里可能要再搞搞

前端代码加密

token存到redis里，真正的退出登录

单点登录

分布式缓存

