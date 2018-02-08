#!/bin/bash
java -jar build/libs/microservice-template-1.0.0.jar &
gradle AcceptanceTest
