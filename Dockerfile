FROM gradle:8.4-jdk17 AS builder
WORKDIR /app

COPY build.gradle .
COPY settings.gradle .
RUN gradle build --no-daemon -x test || true

COPY . .
RUN gradle build --no-daemon -x test

FROM openjdk:17-jdk-slim
VOLUME /tmp
COPY --from=builder /app/build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT [ "java", "-jar", "/app.jar"]