# Etapa 1: Build da aplicação
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copiar arquivos do Maven
COPY pom.xml .
COPY src ./src

# Compilar a aplicação
RUN mvn clean package -DskipTests

# Etapa 2: Imagem de execução
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copiar JAR da etapa de build
COPY --from=build /app/target/*.jar app.jar

# Expor porta da aplicação
EXPOSE 8080

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
