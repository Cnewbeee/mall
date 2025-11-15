# syntax=docker/dockerfile:1

# Runtime image for Java 8 Spring Boot app
FROM eclipse-temurin:8-jre

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="" \
    SPRING_PROFILES_ACTIVE=prod

WORKDIR /app

# Copy built jar (run mvn package beforehand)
COPY target/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
