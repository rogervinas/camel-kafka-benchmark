#!/bin/bash

docker-compose up -d

./gradlew build \
&& java -Djava.awt.headless=false -jar build/libs/*.jar

docker ps -a | grep camel-kafka-benchmark | awk '{print $1}' | xargs docker rm -f
