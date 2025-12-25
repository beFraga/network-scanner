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

# ambiente de produção com porta 8080
ENV APP_ENV=production \
    SERVER_PORT=8080 \
    JAVA_OPTS="-XX:+UseContainerSupport"

# dependências mínimas
RUN apt-get update && apt-get install -y \
    libcap2-bin \
    libcap-dev \
    iproute2 \
    tcpdump \
    && rm -rf /var/lib/apt/lists/*

# usuário não root que executa programa
RUN groupadd -r appuser && useradd -r -g appuser appuser

WORKDIR /app

COPY --from=build /build/capture_service/target/*.jar app.jar

# permissões para usuário e root (usuário não escreve no arquivo)
RUN chown root:root /app/app.jar && \
    chmod 0555 /app/app.jar

# capabilities aplicadas ao Java (captura de pacotes)
RUN setcap cap_net_raw,cap_net_admin=eip $(readlink -f $(which java))

# modo user
USER appuser

EXPOSE ${SERVER_PORT}

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]