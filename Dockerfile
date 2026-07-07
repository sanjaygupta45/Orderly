# syntax=docker/dockerfile:1
# One multi-stage build shared by every OrderFlow service.
# Pass the module name as a build arg, e.g. --build-arg SERVICE=order-service.
FROM maven:3.9-eclipse-temurin-21 AS build
ARG SERVICE
WORKDIR /workspace
COPY . .
# Build just this module and the modules it depends on. The ~/.m2 cache mount is
# reused across all service builds on the same machine, so dependencies download once.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl :${SERVICE} -am -DskipTests clean package

FROM eclipse-temurin:21-jre
ARG SERVICE
WORKDIR /app
COPY --from=build /workspace/${SERVICE}/target/${SERVICE}-1.0.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
