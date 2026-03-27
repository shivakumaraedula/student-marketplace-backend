# ─────────────────────────────────────────────
# Stage 1: Build with Maven (no mvnw needed)
# ─────────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app

# Copy pom.xml first — cache dependency downloads
COPY pom.xml ./
RUN mvn dependency:go-offline -B -q

# Copy source code and build fat JAR
COPY src ./src
RUN mvn clean package -DskipTests -B -q

# ─────────────────────────────────────────────
# Stage 2: Minimal runtime image
# ─────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Copy the fat JAR produced by Maven
COPY --from=builder /app/target/student-marketplace-1.0.0.jar app.jar

# Persistent uploads directory
RUN mkdir -p /app/uploads && chown -R appuser:appgroup /app

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
  "-Dspring.profiles.active=docker", \
  "-Djava.security.egd=file:/dev/./urandom", \
  "-jar", "app.jar"]
