# Dockerfile
FROM openjdk:21-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

ARG JAR_FILE=build/libs/*.jar

# 빌드된 jar 파일 복사
COPY ${JAR_FILE} app.jar

# 포트 오픈 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
