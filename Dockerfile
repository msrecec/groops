FROM maven:3.8-openjdk-11 as build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline
COPY src/ src/
RUN mvn package -DskipTests

# build jre
FROM amazoncorretto:11-alpine as corretto-jdk

RUN apk add --no-cache binutils

RUN jlink \
         --add-modules ALL-MODULE-PATH \
         --strip-debug \
         --no-man-pages \
         --no-header-files \
         --compress=2 \
         --output /jre

# Inject the JAR file into a new container
FROM alpine:latest
WORKDIR /app

ARG uid=1002
ARG gid=1002
ARG user=springuser
ARG group=spring

RUN addgroup -g $gid -S $group
RUN adduser -u $uid -S $user
RUN mkdir logs
RUN chown $user:$group logs

USER $user:$group

ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"

COPY --from=corretto-jdk /jre $JAVA_HOME

COPY VERSION .
COPY --from=build /app/target/*.jar /app/app.jar

EXPOSE 8085

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]