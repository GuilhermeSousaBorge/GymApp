# Stage 1 — Build
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /app

# Copia o pom.xml e baixa as dependências primeiro (cache do Docker)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copia o código fonte e faz o build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2 — Runtime
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia o jar gerado no stage de build
COPY --from=build /app/target/*.jar app.jar

# Porta exposta
EXPOSE 8080

# Inicia a aplicação com o profile de produção
ENTRYPOINT ["java", "-jar", "app.jar", "--spring.profiles.active=prod"]
