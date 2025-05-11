#!/bin/bash
docker-compose -f app-docker-compose.yml -p family-app-infrastracture up -d
docker-compose -f ai-docker-compose.yml -p family-ai-infrastructure up -d