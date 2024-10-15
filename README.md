# LFP Appeals Processor API

## Technologies
- [OpenJDK 21](https://jdk.java.net/archive/)
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

## Terraform ECS

### What does this code do?

The code present in this repository is used to define and deploy a dockerised container in AWS ECS.
This is done by calling a [module](https://github.com/companieshouse/terraform-modules/tree/main/aws/ecs) from terraform-modules. Application specific attributes are injected and the service is then deployed using Terraform via the CICD platform 'Concourse'.


Application specific attributes | Value                                | Description
:---------|:-----------------------------------------------------------------------------|:-----------
**ECS Cluster**        | order-service                                     | ECS cluster (stack) the service belongs to
**Load balancer**      | {env}-chs-internalapi                             | The load balancer that sits in front of the service
**Concourse pipeline**     |[Pipeline link](https://ci-platform.companieshouse.gov.uk/teams/team-development/pipelines/lfp-appeals-api) <br> [Pipeline code](https://github.com/companieshouse/ci-pipelines/blob/master/pipelines/ssplatform/team-development/lfp-appeals-api)                                  | Concourse pipeline link in shared services


### Contributing
- Please refer to the [ECS Development and Infrastructure Documentation](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/4390649858/Copy+of+ECS+Development+and+Infrastructure+Documentation+Updated) for detailed information on the infrastructure being deployed.

### Testing
- Ensure the terraform runner local plan executes without issues. For information on terraform runners please see the [Terraform Runner Quickstart guide](https://companieshouse.atlassian.net/wiki/spaces/DEVOPS/pages/1694236886/Terraform+Runner+Quickstart).
- If you encounter any issues or have questions, reach out to the team on the **#platform** slack channel.

### Vault Configuration Updates
- Any secrets required for this service will be stored in Vault. For any updates to the Vault configuration, please consult with the **#platform** team and submit a workflow request.

### Useful Links
- [ECS service config dev repository](https://github.com/companieshouse/ecs-service-configs-dev)
- [ECS service config production repository](https://github.com/companieshouse/ecs-service-configs-production)
