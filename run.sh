#!/bin/bash
DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$DIR"

# Ensure local java and maven environment is loaded
if [ -f "$HOME/.local/bin/env" ]; then
    . "$HOME/.local/bin/env"
fi

if [ -f "target/skillshare-campus-1.0.0.jar" ]; then
    echo "Starting SkillShare Campus Skill Exchange..."
    java -jar target/skillshare-campus-1.0.0.jar
else
    echo "Building SkillShare package..."
    mvn clean package -DskipTests && java -jar target/skillshare-campus-1.0.0.jar
fi
