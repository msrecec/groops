#!/bin/sh

# Start app in production mode (on your own machine or cloud)

echo "\nStarting app in production mode...\n"

VERSION="$(cat VERSION)" \
docker-compose -f docker-compose.prod.yml up -d
