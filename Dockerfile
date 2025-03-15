FROM openjdk:17-jdk
VOLUME /tmp
COPY build/libs/*SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]