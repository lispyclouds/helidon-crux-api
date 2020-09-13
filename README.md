# Helidon-Crux-API
Demonstrates an API based on Helidon backed by Crux+JDBC

## Requirements
- An OS supported by Java
- Java 14+
- PostgreSQL 11+

## Running locally with Docker
- Easily spin up a latest PostgreSQL container with 
  `docker run -it --name helidon-storage -p 5432:5432 -e POSTGRES_DB=customers -e POSTGRES_USER=helidon -e POSTGRES_PASSWORD=helidon postgres:alpine`
- Clone this repo
- Start the server with `./gradlew run`
- The API is available on `http://0.0.0.0:7777`
