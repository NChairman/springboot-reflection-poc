#!/bin/bash
set -e

# Store current branch to restore later
CURRENT_BRANCH=$(git branch --show-current)

echo "Starting build process..."

# Build ERROR branch
echo "Checking out feature-reflection-param-error..."
git checkout feature-reflection-param-error
echo "Building JAR for error branch..."
./gradlew clean bootJar
echo "Building Docker image: springboot-reflection-poc:feature-reflection-param-error"
docker build -t springboot-reflection-poc:feature-reflection-param-error .

# Build SUCCESS branch
echo "Checking out feature-reflection-param-success..."
git checkout feature-reflection-param-success
echo "Building JAR for success branch..."
./gradlew clean bootJar
echo "Building Docker image: springboot-reflection-poc:feature-reflection-param-success"
docker build -t springboot-reflection-poc:feature-reflection-param-success .

# Restore original branch
echo "Restoring original branch: $CURRENT_BRANCH"
git checkout $CURRENT_BRANCH

# Deploy using Docker Compose
echo "Deploying containers..."
docker-compose -f docker-compose.deploy.yml up -d

echo "Deployment complete!"
echo "Error App running on: http://localhost:8081/demo/fail"
echo "Success App running on: http://localhost:8082/demo/fail"
