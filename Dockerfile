FROM openjdk:14-slim

COPY build/libs/app.jar app.jar

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
