# 基础镜像
FROM eclipse-temurin:17-jdk-jammy
# 设定时区
ENV TZ=Asia/Shanghai
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
# 拷贝项目jar包
COPY /target/point-service-1.0.0-SNAPSHOT.jar /elm-point-app.jar
# 入口点 - 直接在根目录查找配置文件
ENTRYPOINT ["java", "-jar", "/elm-point-app.jar", \
    "--spring.config.location=file:/,classpath:/", \
    "--spring.cloud.bootstrap.location=file:/bootstrap.yml", \
    "--spring.config.name=application,bootstrap"]