# stage 1: 빌드
FROM gradle:8.5-jdk17 AS builder
WORKDIR /workspace
COPY . .
RUN gradle :order:bootJar --no-daemon

# stage 2: 실행
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /workspace/order/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
