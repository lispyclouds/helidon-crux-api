FROM openjdk:15-slim

ARG JAR_PATH="build/libs/app.jar"

COPY $JAR_PATH app.jar

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
