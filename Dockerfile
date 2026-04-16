# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

COPY pom.xml .
RUN mvn -B -q -e -DskipTests dependency:go-offline

COPY src ./src
RUN mvn -B -q -DskipTests package


# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copiar jar
COPY --from=build /app/target/*.jar app.jar

# 🔥 COPIAR WALLET
COPY wallet ./wallet

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]