# 饿了吧

---

本项目起源于tju的软件工程系列实践，是一个使用前后端分离架构完成的饿了吧外卖平台。随着迭代的进行，已分别实现单体架构和微服务架构的后端，`master` 分支最新的前端代码同时支持二者的适配而不需要修改任何代码。

---

## 1 项目结构

PS：打 `*` 表示用于微服务架构

```context
frontend-comprehension/
├── report/                   # 一些文档
├── gra/                      # 文档等中的图片
├── elmclient/                # 前端源码
├── elm_demo/                 # 初始基建版本后端(已废弃)
├── elm_bk/                   # 单体架构后端
├── cloud/                    # *部署用文件
│   ├── images                # 部署用Docker镜像包(太大了,需要自己构建)
│   └── application           # 部属用配置文件
│        ├── nacos            # Nacos 配置中心用配置文件
│        └── shared           # 服务器部署用各个服务的配置文件
├── elm_cloud/                # 微服务架构后端
│   ├── elm-api               # Feign 客户端模块
│   ├── elm-common            # 公共模块
│   ├── elm-gateway           # 网关
│   ├── business-service      # 商铺服务
│   ├── food-service          # 商品服务
│   ├── notification-service  # 消息通知服务
│   ├── order-service         # 订单服务
│   ├── payment-service       # 支付服务
│   ├── point-service         # 积分服务
│   ├── user-service          # 用户服务
│   ├── sql                   # 数据库建库sql文件
│   └── pom.xml               # 父工程依赖
├── .gitignore
└── README.md
```

## 2 启动&部署

### 2.1 已部署信息

**本项目已部署至可用的服务器上** 

- 完整成果：[https://REDACTED_DOMAIN/](https://REDACTED_DOMAIN/)

- 后端接口前缀：[http://REDACTED_DOMAIN:8080](http://REDACTED_DOMAIN:8080) 

- 接口文档路径：[http://REDACTED_DOMAIN:8080/swagger-ui/index.html](http://REDACTED_DOMAIN:8080/swagger-ui/index.html)

**一些测试账号 (非管理员账号可以自行注册)**

普通用户：

- username: common_user
- password: Common123

商家用户：

- username: business_user
- password: Business123

商家端入口位于 **我的** 页面中的 **切换到商家端**

管理员：

- username: admin_user
- password: Admin123

---

### 2.2 前端部分

**所在目录**

`/elmclient`

**技术栈**

- Vue3

- Ngnix

- obfuscate

### 2.2.1 启动

初次运行：

```bash
# 安装依赖
cnpm i
```

启动：

```bash
npm run serve
```

### 2.2.2 服务器上部署

1.构建项目

生成 `/dist`

```bash
cd elmclient
# ---- 普通版本 ---- 
npm run build

# ---- 代码混淆版本 ---- 
# 可能需要补安装依赖
cnpm install javascript-obfuscator@^4.0.0 --save-dev
# 验证
cnpm list javascript-obfuscator webpack-obfuscator
# 构建
npm run build:obfuscate
```

压缩 `/dist` 至 `tar` 格式上传到服务器

2.服务器上部署

基于 `Ngnix`，这里是基于 `Docker` 的容器化部署，挂载的路径替换成你服务器上的路径

注意 `/src/utils/request.js` 中前几行的接口路径 `BASE_URL` `WS_BASE_URL` 可根据实际情况是否支持 `https` 替换，让服务器支持 `https` 可参考一些博客

PS：挂载目录须等根据实际情况，只有第一次部署需要创建容器

**不支持 `https` 版本容器创建：**

```bash
docker run -d \
  --name elm-vue-app \
  -p 80:80 \
  -v /root/elm/nginx.conf:/etc/nginx/nginx.conf:ro \
  -v /root/elm/dist:/usr/share/nginx/html \
  nginx:stable-alpine3.21-perl
```

**支持 `https` 版本容器创建：**

```bash
# 创建网络
docker network create app-network
docker run -d \
  --name nginx-proxy \
  --network app-network \
  -p 80:80 \
  -p 443:443 \
  -v ~/ssl:/etc/nginx/ssl \
  -v ~/nginx-conf:/etc/nginx/conf.d \
  -v ~/elm/dist:/usr/share/nginx/html \
  nginx:latest
```

适配 `SSL` 证书

```bash
mkdir -p ~/nginx-conf
# 创建配置文件
vim ~/nginx-conf/REDACTED_DOMAIN.conf
```

配置文件内容如下：

```
# HTTP -> HTTPS 重定向
server {
    listen 8081;  # 写啥不太重要似乎
    server_name REDACTED_DOMAIN www.REDACTED_DOMAIN; # 换成指定域名
    return 301 https://$server_name$request_uri;
}

# HTTPS 服务器
server {
    listen 443 ssl http2;
    server_name REDACTED_DOMAIN www.REDACTED_DOMAIN;

    # 证书路径（容器内路径）
    ssl_certificate /etc/nginx/ssl/REDACTED_DOMAIN_bundle.crt;
    ssl_certificate_key /etc/nginx/ssl/REDACTED_DOMAIN.key;

    # SSL 配置
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;

    # ========== API 代理配置 (与后端不在统一服务器上时可忽略) ==========
    location /api/ {
        # 转发到后端服务
        proxy_pass http://REDACTED_DOMAIN:8080/api/;

        # 传递真实客户端信息
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # 超时设置
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # 支持 WebSocket
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        proxy_request_buffering off;
    }

    location /ws {
        # 代理到 WebSocket 后端服务
        proxy_pass http://REDACTED_DOMAIN:8080/ws;

        # WebSocket 必需的配置
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";

        # 传递真实客户端信息
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # WebSocket 超时设置（保持长连接）
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 3600s;  # WebSocket 保持连接时间，根据需要调整

        # 关闭缓冲，实时传输
        proxy_buffering off;
        client_max_body_size 100M;
    }

    # 静态文件目录（前端 SPA 应用）
    location / {
        root /usr/share/nginx/html;
        index index.html index.htm;
        try_files $uri $uri/ /index.html;
    }
}
```

每次修改上面的配置文件都需要：

```bash
docker cp ./REDACTED_DOMAIN.conf nginx-proxy:/etc/nginx/conf.d/
docker exec nginx-proxy nginx -t
docker exec nginx-proxy nginx -s reload
```

验证：

```bash
curl -I http://REDACTED_DOMAIN
```

3.后续部署

替换 `/dist`，重启容器

```bash
docker restart <Ngnix容器名>
```

### 2.3 单体架构后端

### 2.3.1 配置信息

**所在目录**

`/elm_bk`

**技术栈**

- SpringBoot

- Maven

- Mybatis

- redis

- rabbitmq

**开发环境**

JDK 17

SpringBoot 3.4.6

Mybatis 3.0.4

MySQL 8.0.40

**配置**

- 数据库
  
  `/sql/elm_v2.sql`

**快速启动**

分支 `mid-comprehension` 开始之后的提交依赖 `redis`、`rabbitmq`，可自行搭建

PS：如使用IDEA在启动或打包项目时遇到控制台报错如 "找不到符号"，请确保以下位置选择正确

![Alt](./gra/fix1.png)

![Alt](./gra/fix2.png)

### 2.3.2 服务器上部署

1.准备镜像

本地打包

```bash
cd elm_bk
docker build -t elm_backend:1.0 .
docker save elm_bk:1.0 -o elm-bk-image.tar
```

上传至服务器...

加载镜像

```bash
docker load -i elm-bk-image.tar
```

2.打包项目

使用 `maven` 打包，生成 `target/elm_bk-0.0.1-SNAPSHOT.jar` 

将 `jar` 包和配置文件 `application.yml` (记得修改成实际部署的配置) 上传至服务器同一目录下，`jar` 包需要重命名为 `app.jar`

3.创建并运行容器(第一次时)

在服务器上 `jar` 包和配置文件的目录下执行：

```bash
docker run -d --name elm-app \
    -p 8080:8080 \
    -v $(pwd)/app.jar:/app.jar \
    -v $(pwd)/application.yml:/application.yml \
    elm_bk:1.0
```

PS：请确保 `8080` 端口开放且不被其他进程占用

查看运行日志：

```bash
docker logs -f elm-app
```

4.后续重新部署

替换 `jar` 包或修改的配置文件，重启容器：

```bash
docker restart elm-app
```

## 2.4 微服务架构后端

### 2.4.1 一些说明

**所在目录**

`/elm_cloud` 

**新增技术栈**

- Spring Cloud

- Gateway

- OpenFeigin

- Nacos

- Caffeine

- Elasticsearch

- ...

**一些大的改进**

- 主从数据库

- Redis+Caffeine 双层缓存

- 积分系统高并发简单支持

- Nacos上配置管理

- 服务治理一系列

### 2.4.2 本地启动

### 2.4.3 服务器上部署

主要描述与单体不一样的地方，其他细节参照单体时的部署

1.中间件准备

必须有的：**redis**、**rabbitmq**、**nacos**、**elasticsearch**

可选：**jaeger**、**sentinel**、**elasticsearch-head**

2.Nacos配置管理准备

所有部署用配置文件存放在`/cloud/application/`下，`/nacos`下的配置文件为nacos配置中心的文件，把其中所有文件上传到 `Nacos` 的配置中心

PS：记得修改中间件的配置信息

3.服务镜像准备

定义各个服务的前缀为order、food等，路由为gateway

**上传镜像至服务器**

```bash
# 构建镜像
docker build -t elm-micro-<服务前缀> .
# 打包镜像
docker save elm-micro-<服务前缀> -o elm-micro-<服务前缀>.tar
```

上传镜像至服务器

**加载镜像**

服务器中

```bash
docker load -i <镜像包tar在服务器上的路径>
```

4.创建并运行容器

- 一般创建一个专门存放这个项目的目录`/elm-micro`

- 在这个目录下创建各个服务专属目录如 `/order-service`等，路由为`/gateway`

- 在对应目录上传jar包、相应的 `application.yml`、`bootstrap`，直接使用的`/cloud/application`中的文件需要去掉文件名的服务前缀；然后jar包需要改名为 `elm-<服务前缀>-app.jar`

- application.yml中有一些参数`elm.nacos.server`需要改成所部署的服务器ip

- 创建并运行容器
  
  如notification-service，容器名一般为 `elm-<服务前缀>-app`，注意有些服务的配置(可看配置文件)如果存在sentinel等服务治理相关的可能需要映射更多的端口，还是注意要与配置文件一致
  
  ```bash
  docker run -d --name elm-notification-app \
  -p 8884:8884 \
  -v $(pwd)/elm-notification-app.jar:/elm-notification-app.jar \
  -v $(pwd)/application.yml:/application.yml \
  -v $(pwd)/bootstrap.yml:/bootstrap.yml \
  elm-micro-notification:2.0.0
  ```

5.后续重新部署

详情见 `/report/description.md`

## 3 迭代记录

**仓库创建**

- 2025-05-12 本仓库创建，开始于前端开发技术实践

**前端开发技术实践**

- 2025-06-15 前端开发技术实践最终版
  
  855118c99b6b834de79cd181daa44fb3a8da8745
  
  保留分支：frontend-comprehension

**软件工程综合实践**

- 2025-09-08 开始
  
  大改框架
  
  9cf74e36a2c986e3929a8bb0d91d69c314c5ed81

- 2025-09-27 课程代码验收版
  
  4811b39329c8ba900ab6f9af88818abaea970d2a

- 2025-10-06 添加服务器配置
  
  9cf1660403b491e5dbe273ef5ae54e1d30fe9667

- 2025-10-08 软件工程综合实践最终版
  
  8f63541c7e2861148d53ea6d732003642b58fa9c
  
  保留分支：comprehension

**软件工程中级实践**

- 2025-10-17 更换服务器配置
  
  af1d1ac56e61b9a3f4c87d4afef4aa2e2d30b0fd

- 2025-11-10 虚拟钱包开始
  
  0aaddb0a3bf536cc800726d5a948dc6c109060d8

- 2025-11-14 虚拟钱包最终版
  
  0a27a93732bfd996522399ff9b158ea32ec77394

- 2025-11-23 积分系统开始
  
  4eeb319c5eb4eccae252fffdf04004a9eb6daf05

- 2025-12-12 积分系统最终版
  
  b85e5e60c423314d371852b9596d1448c680721d
  
  保留分支：mid-comprehension

**软件工程高级实践 (微服务)**

- 2026-3-13 开始 import from https://github.com/Bobchasm/elm-microservices.git
  
  c5502e51e8757b682ee29978040a0f62d824d9a1

- 2026-

## 4 一些说明

欢迎贡献代码、报告问题或提出建议！

### 4.1 提交问题

- 描述问题现象
- 提供复现步骤
- 附上错误日志
- 说明环境信息

### 4.2 提交代码

1. Fork本仓库
2. 创建功能分支
3. 提交代码并编写测试
4. 提交Pull Request

### 4.3 联系方式

如有问题或建议，请通过以下方式联系：

- **邮箱**：<br>
  zengyicydd@tju.edu.cn <br>
  gaocan@tju.edu.cn <br>
  yxy641121@gmail.com <br>
  daimingjing142857@tju.edu.cn <br>
  jsyy@tju.edu.cn

- **Gitee Issues**：提交问题到项目仓库