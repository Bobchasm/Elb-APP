# 基础镜像
FROM eclipse-temurin:17-jdk-jammy
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# 拷贝项目jar包
COPY /target/elm-gateway-1.0.0-SNAPSHOT.jar /elm-gateway-app.jar
# 拷贝 配置文件
COPY /src/main/resources/application.yml /application.yml
# 入口，排除配置文件(部署时外置)
ENTRYPOINT ["java", "-jar", "/elm-gateway-app.jar", "--spring.config.location=/application.yml"]