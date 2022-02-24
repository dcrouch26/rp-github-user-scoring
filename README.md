# rp-scoring

A compojure web application that consumes data from Postgres. Github events are produced into kafka which are written into Postgres.

## Prerequisites

1. Install Docker https://docs.docker.com/get-docker/
2. Kafka and Postgres must be available to use, they've been included in thedockercompose file
3. Build the application with ./build.sh

Clean the environment with
docker-compose rm -svf

You will need [Leiningen][] 2.0.0 or above installed.

## Run the app

 Run the entire stack with docker-compose up

localhost:3000/scores
localhost:3000/events