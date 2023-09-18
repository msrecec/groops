#!/bin/sh

echo "\nBuilding docker image for zipato-plm app...\n"

VERSION="$(cat VERSION)"

docker build -t groops-backend:$VERSION .
