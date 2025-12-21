
# 1. Construção do projeto
FROM maven:3.9.9-eclipse-temurin-17 AS build
LABEL authors="clasen"

WORKDIR /app

# Copiar o proto (o java usará esse proto)
COPY proto /app/proto
COPY Scanner/pom.xml /app/pom.xml

# Gerar código gRPC + builda
COPY Scanner/src /app/src

# 2. Runtime
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]