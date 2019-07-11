#!/bin/bash

docker-compose up -d

./gradlew build \
&& java -Djava.awt.headless=false -jar build/libs/*.jar

docker-compose down