# 1. Build
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
LABEL authors="clasen"

WORKDIR /build

# Pom do pai e módulo pai
COPY pom.xml .
RUN mvn install -N -DskipTests

# Pom do common e módulo common
COPY common/pom.xml ./common/
COPY common/src ./common/src

# Compilar pom sem precisar compilar o do auth_service
RUN mvn -f common/pom.xml install -DskipTests

COPY capture_service/pom.xml ./capture_service/
COPY capture_service/src ./capture_service/src

RUN mvn -f capture_service/pom.xml package -DskipTests

# 2. Runtime
FROM eclipse-temurin:17-jre

RUN apt-get update && apt-get install -y \
    libcap-dev \
    iproute2 \
    tcpdump \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY --from=build /build/capture_service/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]