#!/usr/bin/env bash

docker ps | grep 10ftci | awk '{print $1}' | xargs docker rm -fv
docker-compose -p 10ftci -f deploy/docker-compose.yml up --no-deps -d primary
docker rm -fv 10ftci_data_1
