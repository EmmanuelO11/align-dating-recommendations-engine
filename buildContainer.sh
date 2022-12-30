#!/usr/bin/env bash
chmod +x gradlew
docker build -f Dockerfile --no-cache -t align-dating-backend .
