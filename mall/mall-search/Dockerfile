# syntax=docker/dockerfile:1

FROM eclipse-temurin:8-jre

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

COPY target/*.jar /app/app.jar

EXPOSE 8081

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
