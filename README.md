# 软件工程综合实践饿了么项目

## 1 项目结构

后端代码存放目录  elm_bk

前端代码存放目录  elmclient

## 2 项目部署

注：

1.如需验收整个项目(即前后端完整效果)，请使用我们提供的sql建库并在配置文件中使用它，以下会详细说明

2.以下部署说明主要针对win系统，Linux系统部署方法命令行操作类似

可参考 [从0开始在linux服务器上部署SpringBoot和Vue_vue项目linux部署-CSDN博客](https://blog.csdn.net/m0_53140426/article/details/144745031?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522061248a22aceb1ff2288a8b50a813a59%2522%252C%2522scm%2522%253A%252220140713.130102334.pc%255Fall.%2522%257D&request_id=061248a22aceb1ff2288a8b50a813a59&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~first_rank_ecpm_v1~rank_v31_ecpm-2-144745031-null-null.142^v102^pc_search_result_base7&utm_term=Linux%E9%83%A8%E7%BD%B2spingboot%E5%92%8Cvue&spm=1018.2226.3001.4187)

#### 后端部分

基于 SpringBoot，Maven，Mybatis

##### 环境准备

JDK 17

SpringBoot 3.4.6

Mybatis 3.0.4

##### 配置

- 配置文件数据库修改
  
  ```
  frontend-comprehension/elm_bk/src/main/resources/application.yml
  ```
  
  将数据库配置改为您本地的配置，包括：
  
  url(主要是数据库名)，username，password

- 如果可以使用我们的数据库(为添加拓展功能，我们新增了一些表和字段)，请在本地新建一个名为 **elm_v2** 的数据库，并运行以下路径中的建表语句：
  
  ```
  frontend-comprehension/elm_bk/elm_v2.sql
  ```

##### 运行

- 如果仅在本地测试后端接口
  
  检查完版本(重点检查jdk)并修改数据库配置信息后，在idea中启动项目后端部分:
  
  ```
  frontend-comprehension/elm_bk
  ```

- 如果需要部署
  
  1.使用Maven打包项目，先 clean，再 package，成功控制台如图，生成的项目jar包在:
  
  ```
  frontend-comprehension/elm_bk/target/elm_bk-0.0.1-SNAPSHOT.jar
  ```
  
  ![Alt](./gra/success_package.png)
  
  2.可以将生成的jar包复制到想要的目录中，在jar包存放的目录中打开cmd，执行:
  
  ```bash
  java -jar elm_bk-0.0.1-SNAPSHOT.jar
  ```
  
  如果出现报错 "error='页面文件太小，无法完成操作。' (DOS error/errno=1455)" 请重新执行以下命令:
  
  ```bash
  java -Xmx128m -Xms64m -XX:MaxMetaspaceSize=64m -XX:+UseSerialGC -jar elm_bk-0.0.1-SNAPSHOT.jar
  ```
  
  注：该命令进行了内存配置
  
  3.当终端出现以下界面，则启动成功：
  
  ![Alt](./gra/success_bk_start.png)

### 前端部分

基于 Vue3

##### 环境准备

- 保证npm可用
  
  可参见 [VUE安装及环境配置（完整版）-CSDN博客](https://blog.csdn.net/qq_52611686/article/details/142653081?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522b9220596f6cbffa1a2f0eb8c533005ed%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=b9220596f6cbffa1a2f0eb8c533005ed&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-142653081-null-null.142^v102^pc_search_result_base7&utm_term=vue%E5%AE%89%E8%A3%85%E5%8F%8A%E7%8E%AF%E5%A2%83%E9%85%8D%E7%BD%AE&spm=1018.2226.3001.4187)

- 可参见 [Node.js安装与配置（详细步骤）_nodejs安装及环境配置-CSDN博客](https://blog.csdn.net/qq_42006801/article/details/124830995?ops_request_misc=%257B%2522request%255Fid%2522%253A%2522363c66d0a867fcac896383171b678743%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=363c66d0a867fcac896383171b678743&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~top_positive~default-1-124830995-null-null.142^v102^pc_search_result_base7&utm_term=%E9%85%8D%E7%BD%AEnode&spm=1018.2226.3001.4187)

##### 运行

- 如在ide里直接启动
  
  1.可以使用 Visual Studio Code 打开项目前端部分:
  
  ```
  frontend-comprehension/elmclient
  ```
  
  2.在其终端 TERMINAL 安装依赖 (请先确保具有vue环境与npm):
  
  ```bash
  npm -i
  ```
  
  如果失败，则尝试:
  
  ```bash
  cnpm -i
  ```
  
  3.启动
  
  ```bash
  npm run serve
  ```
  
  当终端出现访问url，则启动成功。访问url即可跳转至项目首页(在此之前请保证后端项目已启动)

- 如要进行部署(已配置node)
  
  以下内容参考 [Vue前端项目部署的三种方案_vue项目部署-CSDN博客](https://blog.csdn.net/qq_44741577/article/details/139236697?ops_request_misc=%257B%2522request%255Fid%2522%253A%252201c3d7a19a919480657fd6784f177099%2522%252C%2522scm%2522%253A%252220140713.130102334..%2522%257D&request_id=01c3d7a19a919480657fd6784f177099&biz_id=0&utm_medium=distribute.pc_search_result.none-task-blog-2~all~sobaiduend~default-1-139236697-null-null.142^v102^pc_search_result_base7&utm_term=%E5%89%8D%E7%AB%AFvue%E9%A1%B9%E7%9B%AE%E9%83%A8%E7%BD%B2&spm=1018.2226.3001.4187)
  
  注：由于前端请求接口时使用本地路径前缀，请将前后端部署在同一台计算机上，保证可以localhost本地访问，否则需要修改前端代码接口请求路径
  
  1.在 **/node/nodejs/** 下新建一个名为 **my_server** 的文件夹，在此文件夹下打开cmd命令行终端 (注：最好使用管理员身份打开)
  
  2.执行：
  
  ```bash
  npm init -y
  ```
  
  出现以下输出则成功：
  
  ![Alt](./gra/init.png)
  
  3.安装快速部署插件 **express**
  
  ```bash
  npm i express
  ```
  
  4.在前面创建的 **my_server** 目录下新建文件 server.js，内容如下:
  
  ```js
  // 引入express
  const express = require("express");
  // 配置端口号
  const PORT = 8081;
  // 创建一个app服务实例
  const app = express();
  // 配置静态资源
  app.use(express.static(__dirname + "/public"));
  
  // 绑定端口监听
  app.listen(PORT, () => {
    console.log(`本地服务器启动成功，http://localhost:${PORT}`);
  });
  ```
  
  5.在 **my_server** 文件夹下新建文件夹 **public**，后续用于存放vue项目的文件
  
  6.使用ide (Visual Studio Code) 或者是命令行终端打开项目的前端部分:
  
  ```
  frontend-comprehension/elmclient
  ```
  
  执行:
  
  ```bash
  npm run build
  ```
  
  项目文件 **elmclient** 下中将出现文件夹 **dist**，此时将 **dist** 下的所有东西复制到前面创建的 **my_server** 文件夹下的 **public** 中
  
  7.在 **my_server** 目录下打开cmd，执行:
  
  ```bash
  node .\server.js
  ```
  
  出现以下输出则启动成功，可以通过输出的url访问到app的首页
  
  ```
  本地服务器启动成功，http://localhost:8081
  ```

## 3 自动测试接口异常情况描述

以下将说明以下老师需要测试接口我们的设计，具体设计在什么情况下响应为 *false*