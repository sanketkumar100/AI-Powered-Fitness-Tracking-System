# =========================
# Build Stage
# =========================
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom first
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Make Maven wrapper executable
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src ./src

# Build Spring Boot application
RUN ./mvnw clean package -DskipTests


# =========================
# Runtime Stage
# =========================
FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy generated JAR from build stage
COPY --from=builder /app/target/*.jar app.jar

# Spring Boot USER service port
EXPOSE 8083

# Start application
ENTRYPOINT ["java", "-jar", "app.jar"]