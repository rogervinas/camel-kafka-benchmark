#!/bin/bash

./gradlew build \
&& java -Djava.awt.headless=false -jar build/libs/*.jar