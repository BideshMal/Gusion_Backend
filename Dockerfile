FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build, skipping tests to speed up deployment
RUN mvn clean package -DskipTests

# Stage 2: Run the App + Docker Daemon (DinD)
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app

# Install Docker CLI, Docker Daemon, and iptables (required for Docker networking)
RUN apt-get update && \
    apt-get install -y docker.io iptables && \
    rm -rf /var/lib/apt/lists/*

# Copy the built jar from the build stage
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080

# Create a startup script to run dockerd AND Spring Boot simultaneously
RUN echo '#!/bin/bash\n\
echo "Starting Docker daemon..."\n\
dockerd > /var/log/dockerd.log 2>&1 &\n\
\n\
# Wait for Docker to be fully ready\n\
while (! docker info > /dev/null 2>&1); do\n\
    echo "Waiting for Docker to start..."\n\
    sleep 1\n\
done\n\
echo "Docker is up and running!"\n\
\n\
# Start the Spring Boot application\n\
echo "Starting Spring Boot..."\n\
exec java -jar app.jar' > start.sh

RUN chmod +x start.sh

# Run the wrapper script
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
