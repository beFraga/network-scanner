# 1. Build
FROM maven:3.9.9-eclipse-temurin-17-alpine AS build

WORKDIR /build

# POM da pasta pai
COPY pom.xml .

# instalar o POM do pai no repositório local do conteiner
RUN mvn install -N -DskipTests
# a partir daqui o pai é conhecido

# Código do common (dependente) e seu POM
COPY common/pom.xml ./common/
COPY common/src ./common/src

# Instala o 'common' no repositório local do conteiner sem olhar para o pai (não precisando compilar o pom.xml do capture)
RUN mvn -f common/pom.xml install -DskipTests

# Código do auth_service e seu POM
COPY auth_service/pom.xml ./auth_service/
COPY auth_service/src ./auth_service/src

# Compila 'auth_service' diretamente
RUN mvn -f auth_service/pom.xml package -DskipTests

# 2. Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=build /build/auth_service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]