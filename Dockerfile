# Stage 1: Build ứng dụng bằng Gradle
FROM gradle:8.5-jdk17 AS build
WORKDIR /app

# Copy các file cấu hình gradle trước để tận dụng cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Cấp quyền thực thi cho gradlew và tải dependencies
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon

# Copy toàn bộ source code và build file jar
COPY src src
RUN ./gradlew build -x test --no-daemon

# Stage 2: Run ứng dụng với Eclipse Temurin JRE (Thay thế cho openjdk)
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy file jar đã build từ stage 1 vào stage này
COPY --from=build /app/build/libs/*-SNAPSHOT.jar app.jar

# Render sẽ cấp một cổng ngẫu nhiên thông qua biến môi trường $PORT
EXPOSE 8080

# Chạy ứng dụng
ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]