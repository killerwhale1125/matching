# 1단계: Gradle로 빌드
FROM gradle:8.5-jdk21 AS builder

WORKDIR /app

# Gradle wrapper와 프로젝트 소스 복사
COPY . .

# gradlew 실행 권한 부여 (리눅스 기반 컨테이너용)
RUN chmod +x ./gradlew

# .jar 파일 생성
RUN ./gradlew clean build --no-daemon

# 2단계: 빌드된 .jar을 실행하는 경량 이미지
FROM openjdk:21

WORKDIR /app

# 빌드된 .jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]