# Intended to be run in the CI

FROM openjdk:14-slim

COPY helidon-crux-jar/app.jar app.jar

ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
