#!/bin/sh

# Stop app in started in development mode (on your own machine)

echo "\Stopping app started in development mode...\n"
docker-compose -f docker-compose.dev.yml down --remove-orphans
