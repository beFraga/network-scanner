# 1. Build
FROM maven:3.9.9-eclipse-temurin-17 AS build
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

# capture_service code and its POM
COPY capture_service/pom.xml ./capture_service/
COPY capture_service/src/ ./capture_service/src

RUN mvn -f capture_service/pom.xml package -DskipTests

# 2. Runtime
FROM eclipse-temurin:17-jre

# production environment with port 8080
ENV APP_ENV=production \
    SERVER_PORT=8081 \
    JAVA_OPTS="-XX:+UseContainerSupport"

# minimum dependencies
RUN apt-get update && apt-get install -y \
    libcap2-bin \
    libcap-dev \
    iproute2 \
    tcpdump \
    && rm -rf /var/lib/apt/lists/*


RUN find "$JAVA_HOME" -name "libjli.so" -exec dirname {} \; > /etc/ld.so.conf.d/java.conf && \
    ldconfig

# non-root user that executes program
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

COPY --from=build /build/capture_service/target/*.jar app.jar
COPY capture_service/entrypoint.sh /entrypoint.sh

# user and root permissions (user doesn't write files)
RUN chown root:root /app/app.jar /entrypoint.sh && \
    chmod 0555 /app/app.jar && \
    chmod 0755 /entrypoint.sh

# capabilities applied to Java (packet capture)
RUN setcap cap_net_raw,cap_net_admin=eip $(readlink -f $(which java))

# user mode
USER appuser

EXPOSE 8081

ENTRYPOINT ["/entrypoint.sh"]