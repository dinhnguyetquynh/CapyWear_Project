# Stage 1: Build ứng dụng bằng Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy toàn bộ mã nguồn vào container
COPY . .

# Dùng thẳng lệnh gradle (có sẵn trong image) thay vì ./gradlew để tránh lỗi CRLF của Windows
# Dấu && rm -f ... giúp xóa file jar rác (nếu Spring Boot sinh ra) để tránh lỗi copy
RUN gradle build -x test --no-daemon && rm -f build/libs/*-plain.jar

# Stage 2: Run ứng dụng với Eclipse Temurin JRE (Nhẹ & Ổn định)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy file jar chính thức đã build từ stage 1 sang stage này
COPY --from=build /app/build/libs/*.jar app.jar

# Cấp cổng cho Render
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]