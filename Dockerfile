## Build image
FROM maven:3-openjdk-11-slim AS builder
WORKDIR /build

COPY pom.xml ./
RUN mvn dependency:resolve && mvn dependency:resolve-plugins

COPY src ./src
RUN mvn package -Dmaven.test.skip=true

## Runtime image
FROM gcr.io/distroless/java:11-debug
WORKDIR /app

COPY --from=builder /build/target/lfp-appeals-api-*.jar /app/lfp-appeals-api.jar

CMD ["/app/lfp-appeals-api.jar"]
