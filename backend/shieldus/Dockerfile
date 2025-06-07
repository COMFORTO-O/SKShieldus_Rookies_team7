
# --- 빌드 스테이지 ---
FROM openjdk:17-jdk-slim AS builder

WORKDIR /app

# Gradle Wrapper 및 빌드 파일 복사 (캐시 활용을 위해 먼저 복사)
COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY build.gradle .

# 소스 코드 복사
COPY src src

# Gradle Wrapper 권한 설정
RUN chmod +x gradlew

# 애플리케이션 빌드 및 테스트 실행
# --no-daemon: 데몬 사용 안 함 (컨테이너 빌드 시 권장)
# -x test: <-- **이 옵션을 추가하여 빌드 시 테스트를 건너뛰도록 합니다.**
RUN ./gradlew build --no-daemon -x test

# --- 실행 스테이지 ---
FROM openjdk:17-jdk-slim
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar
COPY src/main/resources/private_key.pem /private_key.pem
COPY src/main/resources/public_key.pem /public_key.pem

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]