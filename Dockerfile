FROM maven:3.9.9-eclipse-temurin-21 AS builder
WORKDIR /workspace

COPY pom.xml ./
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

RUN useradd --create-home --shell /bin/bash appuser

COPY --from=builder /workspace/target/*.jar /app/app.jar

EXPOSE 8080
ENV SPRING_PROFILES_ACTIVE=docker
ENV JAVA_OPTS=""

USER appuser
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]

