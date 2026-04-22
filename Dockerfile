# --- Stage 1: Build stage ---
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Copy các file cấu hình gradle trước để tận dụng cache
COPY build.gradle settings.gradle /app/
COPY gradle /app/gradle

# Copy toàn bộ source code
COPY . /app

# Build project và bỏ qua chạy test để nhanh hơn
RUN ./gradlew build -x test --no-daemon

# --- Stage 2: Run stage ---
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar đã build từ Stage 1 sang Stage 2
# Lưu ý: Tên file jar thường là {tên_project}-{phiên_ản}.jar
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Mở cổng 8080 (cổng mặc định của Spring Boot)
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-jar", "app.jar"]