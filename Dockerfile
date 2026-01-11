# ---------- Build stage ----------
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY pom.xml .
RUN mvn -B -q dependency:go-offline

COPY src ./src
RUN mvn -B -q clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

RUN useradd -r -u 1001 spring
WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

ENV JAVA_OPTS="\
-XX:MaxRAMPercentage=75 \
-XX:+ExitOnOutOfMemoryError"

ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

USER spring
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
