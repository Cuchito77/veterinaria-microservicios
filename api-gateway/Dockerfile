# ═════════════════════════════════════════════════════
# API GATEWAY · Dockerfile (build multi-stage)
# Etapa 1: compila el jar con Maven (no necesitas Maven local)
# Etapa 2: imagen liviana solo con el JRE 21 y el jar final
# ═════════════════════════════════════════════════════

# ── ETAPA 1: BUILD ───────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiamos primero el pom y bajamos dependencias:
# asi Docker cachea esta capa y no re-descarga todo en cada build.
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Recien ahora copiamos el codigo y compilamos (sin tests).
COPY src ./src
RUN mvn package -DskipTests -B

# ── ETAPA 2: RUNTIME ─────────────────────────────────
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
