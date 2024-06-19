# LFP Appeals Processor API

## Technologies
- [OpenJDK 11](https://jdk.java.net/archive/)
- [Maven](https://maven.apache.org/download.cgi)
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Swagger OpenAPI](https://swagger.io/docs/specification/about/)

## How to run

**To run locally, this application is currently dependent on a local instance of MongoDB to be running at localhost:27017**

## Running Locally With Docker CHS env

1. Clone [Docker CHS Development](https://github.com/companieshouse/docker-chs-development) and follow the steps in the README.

2. Enable the `lfp-appeals` module

3. Navigate to `http://api.chs.local:9000`

Development mode is available for this service in [Docker CHS Development](https://github.com/companieshouse/docker-chs-development).

    ./bin/chs-dev development enable lfp-appeals-api

Swagger documentation is available for this service in the the docker CHS development

1. Navigate to `http://api.chs.local/api-docs/lfp-appeals-api/swagger-ui.html`
## Running locally without docker CHS env

**To run locally, this application is currently dependent on a local instance of MongoDB to be running at localhost:27017**

### Run MongoDB 
           
1. Deploy an instance of MongoDB in a docker container: 

    `docker run -p 27017:27017 --name mongodb -d mongo:3.6`

### Connect to your MongoDB instance

1. Access the bash shell inside the mongo container:

    `docker exec -it mongodb bash`

2. Start mongo:

    `mongo`

3. Show databases:

    `show dbs`

4. Switch to 'test' database (for example):

    `use test`

5. Retrieve all documents in the 'appeals' collection:

    `myCursor = db.appeals.find( {} )`

### Run Spring boot

1. `mvn clean install spring-boot:run`

2. Navigate to the running app in a browser: 

    `http://localhost:9000`


## Useful Endpoints

### Health

http://localhost:9000/actuator/health

### API Documentation (Swagger)

http://localhost:9000/swagger-ui.html
