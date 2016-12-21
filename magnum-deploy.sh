#!/usr/bin/env bash

export DOCKER_HOST=tcp://bld-magnumci-01:2375
docker-compose -f ci/deploy/docker-compose.yml pull
docker-compose -p cici -f ci/deploy/docker-compose.yml up --no-deps --force-recreate -d primary
