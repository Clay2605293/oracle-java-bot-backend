#!/bin/bash

export IMAGE_NAME=oracle-java-bot
export IMAGE_VERSION=0.1

# Obtener registry
if [ -z "$DOCKER_REGISTRY" ]; then
    echo "Error: DOCKER_REGISTRY no está definido"
    exit 1
fi

export IMAGE=${DOCKER_REGISTRY}/${IMAGE_NAME}:${IMAGE_VERSION}

echo "🔨 Building Docker image..."
docker build -t $IMAGE .

echo "📤 Pushing image..."
docker push $IMAGE

if [ $? -eq 0 ]; then
    echo "🧹 Cleaning local image..."
    docker rmi "$IMAGE"
fi

echo "✅ Build completado"