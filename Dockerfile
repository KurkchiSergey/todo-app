# ---------- Стадия 1: сборка ----------
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Сначала только pom.xml
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Затем исходники и сборка (тесты в CI уже прогнаны)
COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Стадия 2: запуск (лёгкий образ только с JRE) ----------
FROM eclipse-temurin:17-jre
WORKDIR /app

# Запуск под непривилегированным пользователем
RUN useradd -r -u 1001 appuser
COPY --from=build /app/target/*.jar app.jar
USER appuser

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
