#!/bin/bash

# Gradle wrapper script
# This is a simplified version for the project

if [ ! -d ".gradle" ]; then
    echo "Initializing Gradle..."
fi

# Run gradle command
./gradlew "$@"
