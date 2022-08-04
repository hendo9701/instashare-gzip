# instashare-gzip

![example workflow](https://github.com/hendo9701/instashare-gzip/actions/workflows/main.yml/badge.svg)
![quality-score](https://api.codiga.io/project/34292/score/svg)
![code-grade](https://api.codiga.io/project/34292/status/svg)
## Description

This is a worker service for compressing files. The service listens from a named exchange in a message broker for file
upload events. When the events arrive, the service creates a compressed version of the file in disk. Then it uploads
that version to the cloud storage, updating the necessary data related to the file.

## Bootstrapping the service

1. Run ``docker-compose up`` from the project's root folder
1. Run ``mvn spring-boot:run``

## Running the tests

Run ``mvn clean test``


