# Stage 1: Build ứng dụng bằng Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Giới hạn RAM cho Gradle để không bị Render tự động ngắt (chống lỗi OOM)
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx256m"

# Copy toàn bộ mã nguồn (đã được lọc bởi .dockerignore)
COPY . .

# Build ứng dụng
RUN gradle build -x test --no-daemon && rm -f build/libs/*-plain.jar

# Stage 2: Run ứng dụng với Eclipse Temurin JRE
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy file jar chính thức đã build từ stage 1 sang
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]