# HealthData-Backend

고객 건강 활동 데이터(걸음 수, 소모 칼로리, 이동 거리)를 수집하고 분석하는 Spring Boot 기반 백엔드 애플리케이션입니다. Kafka를 이용한 비동기 데이터 처리를 통해 대용량 트래픽에 안정적으로 대응하도록 설계되었습니다.

## ✨ 주요 기능

- **JWT 기반 사용자 인증**: Spring Security를 이용한 안전한 회원가입 및 로그인 기능을 제공합니다.
- **비동기 데이터 처리**: Kafka 메시지 큐를 통해 대량의 활동 데이터를 비동기적으로 수집하여 시스템 부하를 최소화합니다.
- **활동 데이터 통계**: 사용자의 활동 데이터를 기반으로 일별/월별 통계를 조회하는 API를 제공합니다.
- **JPA 최적화**: JPQL을 사용하여 데이터베이스 레벨에서 데이터를 집계하여 조회 성능을 최적화합니다.
- **인덱싱 전략**: 핵심 조회 경로에 복합 인덱스를 적용하여 빠른 데이터 조회를 보장합니다.

## 🛠️ 기술 스택

- **Backend**: Java 17, Spring Boot 3.x, Spring Security, Spring Data JPA
- **Database**: MySQL 8.x
- **Message Queue**: Kafka
- **Build Tool**: Gradle

## ⚙️ 시작하기

### 사전 요구사항

- JDK 17
- Gradle 8.x
- MySQL 8.x
- Kafka 3.x

### 설치 및 설정

1.  **프로젝트 클론**
    ```bash
    git clone https://github.com/your-username/healthdata.git
    cd healthdata
    ```

2.  **데이터베이스 및 Kafka 설정**
    - MySQL에 접속하여 데이터베이스를 생성합니다.
      ```sql
      CREATE DATABASE healthdata_db;
      ```
    - `src/main/resources/application.properties` 파일을 열어 본인의 환경에 맞게 데이터베이스와 Kafka 정보를 수정합니다.
      ```properties
      # Database
      spring.datasource.url=jdbc:mysql://localhost:3306/healthdata_db
      spring.datasource.username=your_mysql_username
      spring.datasource.password=your_mysql_password

      # Kafka
      spring.kafka.bootstrap-servers=localhost:9092
      ```

### 애플리케이션 실행

- **개발 환경**
  ```bash
  ./gradlew bootRun
  ```
- **배포 환경**
  ```bash
  # 1. 프로젝트 빌드 (jar 파일 생성)
  ./gradlew build

  # 2. 애플리케이션 실행
  java -jar build/libs/healthdata-0.0.1-SNAPSHOT.jar
  ```

## 📖 API 명세

**Swagger URL**: `http://localhost:8080/swagger-ui/index.html`