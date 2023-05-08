#!/bin/sh

echo "\nCleaning all docker containers, images and volumes from groops...\n"

IMAGES=$(docker images -q groops)

docker container rm -f groops-postgres
docker volume rm groops-pgdata
if [ "$IMAGES" ]; then
    docker image rm -f $IMAGES
fi
