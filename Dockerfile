#FROM tomcat:10.1-jdk21
#RUN rm -rf /usr/local/tomcat/webapps/*
#COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
#EXPOSE 8080
#CMD ["catalina.sh", "run"]

# ==========================================
# GIAI ĐOẠN 1: Build ứng dụng (Builder)
# ==========================================
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# 1. Copy các file cấu hình Gradle trước để tận dụng Docker Cache
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# Cấp quyền thực thi và tải trước dependencies (giúp build nhanh hơn ở các lần sau)
RUN chmod +x ./gradlew
RUN ./gradlew dependencies --no-daemon || true

# 2. Copy source code và tiến hành build
COPY src src
RUN ./gradlew clean bootJar -x test --no-daemon

# ==========================================
# GIAI ĐOẠN 2: Chạy ứng dụng (Runner)
# ==========================================
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy file JAR thành phẩm từ giai đoạn builder sang
# Spring Boot Gradle mặc định tạo file trong build/libs/
COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]