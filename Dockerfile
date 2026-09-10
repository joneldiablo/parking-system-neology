# ─── BUILD: compila el monorepo en un solo JAR (admin + kiosko + API) ─────────
FROM maven:3.9-eclipse-temurin-21 AS build

# Node.js requerido por el perfil prod de Maven para compilar el frontend Angular
ENV NODE_MAJOR=22
RUN curl -fsSL "https://deb.nodesource.com/setup_${NODE_MAJOR}.x" | bash - \
    && apt-get install -y --no-install-recommends nodejs \
    && rm -rf /var/lib/apt/lists/* \
    && node -v && npm -v

WORKDIR /app
COPY . .
RUN cd backend && mvn -Pprod clean package -DskipTests

# ─── RUN: solo el JAR con JRE mínima ─────────────────────────────────────────
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/backend/target/parking-system-1.0.0.jar app.jar
ENV SERVER_PORT=8082
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]