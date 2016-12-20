#!/usr/bin/env bash

docker rm -fv `docker ps -aq`
docker-compose -p 10ftci -f deploy/docker-compose.yml up --no-deps -d primary
docker rm -fv 10ftci_data_1
