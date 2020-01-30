# LFP Appeals Processor API

## Technologies
- [OpenJDK 11](https://jdk.java.net/archive/)
- [Maven](https://maven.apache.org/download.cgi)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Swagger OpenAPI](https://swagger.io/docs/specification/about/)

## How to run

### Run as a Spring Boot application

Using local profile:
`mvn clean install spring-boot:run -Dspring.profiles.active=local`

### Run with MongoDB

This application uses MongoDB as it's datastore, to run MongoDB:

1. Deploy an instance of MongoDB in a docker container `docker run -p 27017:27017 --name mongodb -d mongo:3.6` 

## Useful Endpoints

### Health

http://localhost:9000/actuator/health

### API Documentation (Swagger)

http://localhost:9000/swagger-ui.html
