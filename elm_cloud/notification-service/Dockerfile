# 基础镜像
FROM eclipse-temurin:17-jdk-jammy
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# 拷贝项目jar包
COPY /target/notification-service-1.0.0-SNAPSHOT.jar /elm-notification-app.jar
ENTRYPOINT ["java", "-jar", "/elm-notification-app.jar", \
    "--spring.config.location=file:/,classpath:/", \
    "--spring.cloud.bootstrap.location=file:/bootstrap.yml", \
    "--spring.config.name=application,bootstrap"]