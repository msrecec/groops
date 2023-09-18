#!/bin/sh

echo "\nBuilding docker image for groops app...\n"

VERSION="$(cat VERSION)"

docker build -t groops-backend:$VERSION .
