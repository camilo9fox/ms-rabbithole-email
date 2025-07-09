# -------- Build stage --------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build
COPY pom.xml .
COPY src ./src
# Produce an optimized runnable jar (skip tests for faster CI)
RUN mvn -q clean package -DskipTests

# -------- Runtime stage ------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /build/target/email-*.jar app.jar

# Environment variables are injected at runtime via docker-compose or docker run
ENV JAVA_OPTS=""

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]
