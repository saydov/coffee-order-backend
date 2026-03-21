# syntax=docker/dockerfile:1.7

FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml ./
COPY coffee-shared/pom.xml coffee-shared/
COPY coffee-admin/pom.xml coffee-admin/
COPY coffee-board/pom.xml coffee-board/
COPY coffee-client/pom.xml coffee-client/
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -pl coffee-shared,coffee-admin,coffee-board,coffee-client -am \
        dependency:go-offline

COPY coffee-shared/src coffee-shared/src
COPY coffee-admin/src  coffee-admin/src
COPY coffee-board/src  coffee-board/src
COPY coffee-client/src coffee-client/src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -DskipTests package

FROM eclipse-temurin:21-jre-alpine AS runtime
ARG MODULE
ENV MODULE=${MODULE}
WORKDIR /app

RUN addgroup -S coffee && adduser -S coffee -G coffee
COPY --from=build /workspace/${MODULE}/target/${MODULE}-*.jar /app/app.jar
RUN chown -R coffee:coffee /app
USER coffee

ENV JAVA_OPTS=""
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]
