# stage 1: 빌드
FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle :payment:bootJar --no-daemon

# stage 2: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/payment/build/libs/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]
