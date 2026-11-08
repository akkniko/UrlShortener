#!/usr/bin/env sh
set -eu

if [ -f .env ]; then
  set -a
  # .env is a trusted local configuration file maintained by the developer.
  . ./.env
  set +a
fi

docker compose up -d --wait
./mvnw clean package
exec java -jar target/url-shortener.jar
