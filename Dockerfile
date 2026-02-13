# Stage 1: Build the App
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Run the App + Docker Daemon (DinD)
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

RUN apt-get update && \
    apt-get install -y docker.io iptables && \
    rm -rf /var/lib/apt/lists/*

COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# 🟢 UPDATED: Added a timeout and error dump to break the infinite loop
RUN echo '#!/bin/bash\n\
echo "Starting Docker daemon..."\n\
dockerd > /var/log/dockerd.log 2>&1 &\n\
\n\
TIMEOUT=15\n\
while (! docker info > /dev/null 2>&1); do\n\
    echo "Waiting for Docker to start..."\n\
    sleep 1\n\
    TIMEOUT=$((TIMEOUT-1))\n\
    if [ $TIMEOUT -eq 0 ]; then\n\
        echo "🚨 DOCKER FAILED TO START. ERROR LOGS:"\n\
        cat /var/log/dockerd.log\n\
        break\n\
    fi\n\
done\n\
\n\
echo "Starting Spring Boot..."\n\
exec java -jar app.jar' > start.sh

RUN chmod +x start.sh
ENTRYPOINT ["./start.sh"]

# Stage 1: Build the App
# FROM maven:3.9.6-eclipse-temurin-21 AS build
# WORKDIR /app
# COPY pom.xml .
# COPY src ./src
# # Build, skipping tests to speed up deployment
# RUN mvn clean package -DskipTests

# Stage 2: Run the App
# FROM eclipse-temurin:21-jdk-alpine
# WORKDIR /app
# COPY --from=build /app/target/*.jar app.jar
# EXPOSE 8080
# ENTRYPOINT ["java", "-jar", "app.jar"]
# Stage 1: Build the App
