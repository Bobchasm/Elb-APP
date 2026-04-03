# 基础镜像
FROM eclipse-temurin:17-jdk-jammy
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# 拷贝项目jar包
COPY /target/payment-service-1.0.0-SNAPSHOT.jar /elm-payment-app.jar
## 拷贝 配置文件
#COPY /src/main/resources/application.yml /application.yml
#COPY /src/main/resources/bootstrap.yml /bootstrap.yml
# 入口，排除配置文件(部署时外置)
#ENTRYPOINT ["java", "-jar", "/elm-payment-app.jar", "--spring.config.location=/application.yml,/bootstrap.yml"]
ENTRYPOINT ["java", "-jar", "/elm-payment-app.jar"]