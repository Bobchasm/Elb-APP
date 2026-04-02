# MySQL主从重建记录

## 背景与目标

- 旧MySQL容器已被勒索攻击，数据不可用。
- 采用方案：同一台服务器上使用两个MySQL Docker容器搭建主从，使用不同的端口。
- 同时将微服务从单数据源改为主从双数据源（读写分离）。
- 本文记录了操作步骤、代码变更、验证方式与注意事项。

---

## 一、事故处置与重建思路

### 1.1 处置方式

- 删掉旧容器

### 1.2 采用的数据库架构

- 主库容器：`mysql-master`，对外端口 `3306`
- 从库容器：`mysql-slave`，对外端口 `3307`
- 同机部署，Docker网络互通。

---

## 二、服务器侧操作步骤（执行记录）

> 说明：以下为执行方案与操作顺序记录，用于复盘与后续复用。

### 2.1 清理旧环境

- 停止并删除旧MySQL容器（被勒索容器）。

### 2.2 创建目录结构

- `/opt/mysql/master/conf`
- `/opt/mysql/master/data`
- `/opt/mysql/slave/conf`
- `/opt/mysql/slave/data`
- `/opt/mysql/initdb`

### 2.3 配置主从 `my.cnf`

- 主库开启：
  - `server-id=1`
  - `log-bin`
  - `binlog-format=ROW`
  - `gtid-mode=ON`
- 从库开启：
  - `server-id=2`
  - `relay-log`
  - `read-only=1`
  - `gtid-mode=ON`

### 2.4 准备初始化SQL

- 使用项目目录 `elm_cloud/sql/` 下脚本：
  - `user.sql`
  - `order.sql`
  - `food.sql`
  - `business.sql`
  - `wallet.sql`
  - `points_system.sql`
  - `notification.sql`

### 2.5 启动主从容器

- 使用 `docker-compose.yml` 启动 `mysql-master` 与 `mysql-slave`。
- 主库挂载 `initdb`，首次启动自动建库建表。

### 2.6 初始化与复制配置

- 在主库创建复制用户 `repl`。
- 在主库创建业务连接用户 `elm_app` 并授权到各业务库。
- 全量数据从主库导出并导入到从库。
- 从库执行 `CHANGE REPLICATION SOURCE TO ...` + `START REPLICA`。
- `SHOW REPLICA STATUS\G` 检查复制状态为正常。

---

## 三、新账号密码

- **MySQL Root**
  - 用户名：`root`
  - 密码：`Elm@2026Secure!`

- **微服务业务账号（主从连接）**
  - 用户名：`elm_app`
  - 密码：`REDACTED_PASSWORD`

- **主从复制账号（仅复制使用）**
  - 用户名：`repl`
  - 密码：`Repl@2026!`

---

## 四、代码与配置改动

## 4.1 Maven依赖改造

### 文件：`elm_cloud/pom.xml`

- `dynamic-datasource` 版本：
  - 从：`2.5.6`
  - 到：`4.3.1`
- 依赖artifact改为Spring Boot 3版本：
  - 从：`dynamic-datasource-spring-boot-starter`
  - 到：`dynamic-datasource-spring-boot3-starter`
- 移除不存在模块：
  - 删除 `<module>ai-service</module>`（目录不存在导致构建失败）

### 文件：`elm_common/pom.xml`

- 依赖artifact改为：
  - `dynamic-datasource-spring-boot3-starter`
- 删除 `optional`，保证下游服务都能获得该依赖。

### 文件：`business-service/pom.xml`

- 删除了旧版重复声明：
  - `dynamic-datasource-spring-boot-starter:2.5.6`

---

## 4.2 共享数据源配置（Nacos模板）

### 文件：`cloud/application/nacos/shared-mybatis.yaml`

- 从单数据源：
  - `spring.datasource.url/username/password`
- 改为双数据源：
  - `spring.datasource.dynamic.datasource.master`
  - `spring.datasource.dynamic.datasource.slave`
- 增加主从URL、账号、密码占位，兼容服务变量：
  - 主：`3306`
  - 从：`3307`

---

## 4.3 各服务开发环境配置（application-dev.yml）

以下7个服务已统一更新：

- `user-service/src/main/resources/application-dev.yml`
- `order-service/src/main/resources/application-dev.yml`
- `food-service/src/main/resources/application-dev.yml`
- `business-service/src/main/resources/application-dev.yml`
- `payment-service/src/main/resources/application-dev.yml`
- `point-service/src/main/resources/application-dev.yml`
- `notification-service/src/main/resources/application-dev.yml`

统一改动内容：

- DB账号从 `root` 改为 `elm_app`
- DB密码从 `123456` 改为 `REDACTED_PASSWORD`
- 新增从库参数：
  - `elm.db.slave.port: 3307`
  - `elm.db.slave.username: elm_app`
  - `elm.db.slave.pw: REDACTED_PASSWORD`

---

## 4.4 各服务组合环境配置（application-com.yml）

以下7个服务已从单数据源改为动态双数据源：

- `user-service/src/main/resources/application-com.yml`
- `order-service/src/main/resources/application-com.yml`
- `food-service/src/main/resources/application-com.yml`
- `business-service/src/main/resources/application-com.yml`
- `payment-service/src/main/resources/application-com.yml`
- `point-service/src/main/resources/application-com.yml`
- `notification-service/src/main/resources/application-com.yml`

改造点：

- `spring.datasource` 替换为 `spring.datasource.dynamic`
- 主库使用 `master`，从库使用 `slave`
- JDBC URL增加 `useUnicode`、`characterEncoding`、`serverTimezone`
- `notification-service` 原硬编码 `root/123456` 已替换为 `elm_app/REDACTED_PASSWORD`

---

## 4.5 读写分离代码新增

### 新增文件：`elm-common/src/main/java/config/ReadWriteSplitInterceptor.java`

作用：

- MyBatis拦截器按SQL类型自动切换数据源：
  - `SELECT` -> `slave`
  - `INSERT/UPDATE/DELETE` -> `master`
- 事务内强制走 `master`
- 若已手工指定数据源（上下文已有值），则不覆盖。

### 新增文件：`elm-common/src/main/java/config/DataSourceConfig.java`

作用：

- 注册 `ReadWriteSplitInterceptor` 为Spring Bean。

---

## 五、构建与部署过程（就打了个jar包）

### 5.1 构建报错与处理

问题：

- 根POM包含不存在模块 `ai-service` 导致Maven无法读取项目。
- PowerShell中将 `-pl` 参数换行误执行导致命令无效。

处理：

- 已从 `pom.xml` 移除 `<module>ai-service</module>`。
- 使用单行Maven命令构建成功。

建议构建命令（已验证可用）：

```powershell
mvn -f "c:\Users\Administrator\Desktop\elm_micro\frontend-comprehension\elm_cloud\pom.xml" clean package -DskipTests -pl elm-common,elm-api,elm-gateway,user-service,order-service,business-service,food-service,point-service,payment-service,notification-service -am
```

### 5.2 上传路径不存在说明

- 发现 `elm-user-app` 不存在，遂放弃。

---

## 六、验证建议

1. 数据库主从
   - 主库写入测试数据，从库可读到。
   - `SHOW REPLICA STATUS\G` 正常。

2. 服务启动
   - 各服务日志无数据源初始化异常。
   - Nacos中服务实例全部健康。

3. 读写分离
   - 读请求走从库，写请求走主库（可通过日志或数据库general log抽样核验）。



