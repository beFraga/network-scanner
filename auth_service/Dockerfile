# 1. Build
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build
LABEL authors="clasen"

WORKDIR /build

# father POM
COPY pom.xml .

# install father's POM on conteiner local repository
RUN mvn install -N -DskipTests

# Common code (dependent) and its POM
COPY common/pom.xml ./common/
COPY common/src ./common/src

# Install 'common' on conteiner local repository
RUN mvn -f common/pom.xml install -DskipTests

# auth_service code and its POM
COPY auth_service/pom.xml ./auth_service/
COPY auth_service/src/ ./auth_service/src

# Compile 'auth_service' diretcly
RUN mvn -f auth_service/pom.xml package -DskipTests

# 2. Runtime
FROM eclipse-temurin:17-jre-alpine

# user environment with port 8080
ENV APP_ENV=production \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport"

# add a non-root user
RUN addgroup -S appuser && adduser -S -G appuser appuser

WORKDIR /app

COPY --from=build /build/auth_service/target/*.jar /app/
RUN mv /app/$(ls /app/ | grep -v "plain" | head -n 1) /app/app.jar

# user and root permissions (user doesn't write files)
RUN chown root:root /app/app.jar && \
    chmod 0555 /app/app.jar

# user mode
USER appuser

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]