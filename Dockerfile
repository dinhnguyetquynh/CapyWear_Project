# Stage 1: Build ứng dụng bằng Gradle (Sử dụng JDK 21)
FROM gradle:8.5-jdk21 AS build
WORKDIR /app

# Giới hạn RAM cho Gradle để tránh quá tải trên Render
ENV GRADLE_OPTS="-Dorg.gradle.jvmargs=-Xmx256m"

# Copy toàn bộ mã nguồn (nhớ giữ file .dockerignore để bỏ qua thư mục rác)
COPY . .

# Build ứng dụng
RUN gradle build -x test --no-daemon && rm -f build/libs/*-plain.jar

# Stage 2: Run ứng dụng với Eclipse Temurin JRE 21 (Nhẹ & Ổn định)
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy file jar chính thức đã build từ stage 1 sang
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-Dserver.port=${PORT:8080}", "-jar", "app.jar"]