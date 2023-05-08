#!/bin/sh

# Start app in development mode (on your own machine)

echo "\nStarting app in development mode...\n"

JH=${1:-$JAVA17_HOME}
JH=${JH:-$JAVA_HOME}

docker-compose up -d --remove-orphans
rm -r target
JAVA_HOME=$JH ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
