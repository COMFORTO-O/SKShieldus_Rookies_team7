# 도메인 설계서

## 문서 정보

- **프로젝트명**: 소켓 기반 코드 조율[선생님] (연결 지향)
- **작성자**: [SK쉴더즈 루키즈 7조]

---

## 1. 프로젝트 개요

### 1.1 프로젝트 목적

- 코딩 테스트 문제에 대해서 사용자의 피드백 실시간 검토

**예시:**
- 코딩 테스트 문제 실시간 공유 및 사용자의 코드 제출에 대해 실시간 피드백
- 이를 통해 사용자와 피드백 제공자 간의 실시간 인터랙션을 기반으로 학습효율성, 피드백 반영속도 상승

---

# Entity 설계서 – Shieldus Online-Judge

## 문서 정보

| 구분 | 값 |
|------|----|
| **프로젝트명** | Shieldus |
| **작성자** | Platform-Backend Team |
| **작성일** | 2025-06-05 |

---

## 1. Entity 설계 개요

### 1.1 설계 목적

온라인 코딩 테스트/채점 플랫폼 Shieldus 의 비즈니스 도메인을 JPA Entity로 모델링하여  
객체 ↔ 관계 매핑을 명확히 하고, 추후 도메인 주도 개발(DDD) 와 유지보수를 용이하게 한다.

### 1.2 설계 원칙

- SRP(단일 책임), 캡슐화
- 불변 필드 선호, 소프트-삭제 적용
- 연관관계는 필요 최소 + LAZY 기본
- Auditing, Validation, Index 우선 설계

### 1.3 기술 스택

| 영역 | 사용 버전 |
|------|------------|
| Language / Build | Java 17, Gradle 8 |
| Framework | Spring Boot 3.2, Spring Data JPA |
| DBMS | MariaDB 10.11 |
| Query | QueryDSL 5 |
| Validation | Jakarta Bean-Validation 3 |
| Security | Spring Security 6 + JWT (HMAC512) |
| Infra | Docker, NGINX |
| CI/CD | GitHub Actions |

---

## 2. Entity 목록 및 분류

| Entity | 유형 | 비즈니스 중요도 | 기술 복잡도 | 연관 | 우선순위 |
|--------|------|------------------|----------------|------|----------|
| Member | 핵심 | ★★★★☆ | ★★☆☆☆ | 7 | 1 |
| Problem | 핵심 | ★★★★☆ | ★★★☆☆ | 6 | 1 |
| ProblemCode | 지원 | ★★★☆☆ | ★☆☆☆☆ | 2 | 1 |
| ProblemTestCase | 핵심 | ★★★★☆ | ★★☆☆☆ | 2 | 1 |
| MemberSubmitProblem | 핵심 | ★★★★☆ | ★★★☆☆ | 4 | 1 |
| MemberTempCode | 지원 | ★★★☆☆ | ★★☆☆☆ | 1 | 2 |
| ProblemComments | 지원 | ★★☆☆☆ | ★☆☆☆☆ | 2 | 3 |
| ProblemAnswerList | 이력 | ★★☆☆☆ | ★☆☆☆☆ | 2 | 3 |
| Room | 실시간 | ★★★☆☆ | ★★☆☆☆ | 1 | 2 |

> ※ 기타 Enum, VO, Auditing Base 제외

---

## 2.2 상속 구조 (BaseEntity)

```plantuml
classDiagram
class BaseEntity {
  <<abstract>>
  +Long id
  +LocalDateTime createdAt
  +LocalDateTime updatedAt
}
BaseEntity <|-- Member
BaseEntity <|-- Problem
BaseEntity <|-- ProblemCode
BaseEntity <|-- ProblemTestCase
BaseEntity <|-- MemberSubmitProblem
BaseEntity <|-- MemberTempCode
BaseEntity <|-- ProblemComments
BaseEntity <|-- ProblemAnswerList
```

---

## 3. 공통 설계 규칙

| 항목 | 규칙 |
|------|------|
| 테이블명 | snake_case 복수형 (e.g. members) |
| 컬럼명 | snake_case |
| PK 생성 | IDENTITY (Long, AI) |
| FK 명 | {referenced_column}_id |
| Boolean | is_deleted, pass |
| Auditing | created_at, updated_at (BaseEntity) |
| Soft-Delete | is_deleted + deleted_at |

---

## 4. 상세 Entity 설계

### 4.1 Member

| 필드 | 타입 | 컬럼 | 제약 | 설명 | 규칙 |
|------|------|--------|------|------|------|
| id | Long | id | PK | 회원 PK | |
| role | Enum(MemberRoleEnum) | role | NOT NULL | USER / ADMIN | ROLE_X 권한 자동 부여 |
| email | String(100) | email | UNIQUE | 로그인 ID | 중복 금지 |
| password | String(255) | password | NOT NULL | BCrypt 암호화 | |
| name | String(50) | name | NOT NULL | 닉네임 | |
| phone | String(15) | phone | NOT NULL | 010… | |
| memberRank | Integer | member_rank | DEFAULT 0 | 랭킹 포인트 | 제출 통과 시 +1 |
| isDeleted | Boolean | is_deleted | DEFAULT false | 소프트 삭제 | true → 로그인 차단 |
| deletedAt | LocalDateTime | deleted_at | | 삭제 시각 | |

### 4.2 ProblemCode

| 필드 | 타입 | 컬럼 | 제약 | 설명 |
|------|------|--------|------|------|
| id | Long | id | PK | 카테고리 PK |
| code | String(30) | code | UNIQUE | “JAVA”, “PYTHON”, … |
| description | String(100) | description | | 화면 표시용 |

### 4.3 Problem

| 필드 | 타입 | 컬럼 | 제약 | 설명 |
|------|------|--------|------|------|
| id | Long | id | PK | |
| member | FK → members.id | member_id | NULLABLE | 작성자 |
| title | String(150) | title | NOT NULL | 문제 제목 |
| detail | TEXT | detail | NOT NULL | Markdown 본문 |
| category | FK → problem_code.id | problem_code_id | NOT NULL | 언어/분류 |
| level | Integer | level | NOT NULL | 0 ~ 5 |
| isDeleted | Boolean | is_deleted | DEFAULT false | Soft Delete |
| deletedAt | LocalDateTime | deleted_at | | 삭제 시각 |

### 4.4 ProblemTestCase

| 필드 | 타입 | 컬럼 | 제약 | 설명 |
|------|------|--------|------|------|
| id | Long | id | PK | |
| problem | FK → problem.id | problem_id | NOT NULL | 소속 문제 |
| isTestCase | Boolean | is_test_case | DEFAULT false | 채점용 여부 |
| input | TEXT | input | NOT NULL | stdin |
| output | TEXT | output | NOT NULL | expected stdout |

### 4.5 MemberSubmitProblem

| 필드 | 타입 | 컬럼 | 제약 | 설명 |
|------|------|--------|------|------|
| id | Long | id | PK | |
| member | FK → members.id | member_id | NOT NULL | 제출자 |
| problem | FK → problem.id | problem_id | NOT NULL | 문제 |
| pass | Boolean | pass | DEFAULT false | 모든 TC 통과 |
| completedAt | LocalDateTime | completed_at | | 통과 시각 |
| createdAt / updatedAt | LocalDateTime | created_at / updated_at | Auditing | |

### 4.6 MemberTempCode

| 필드 | 타입 | 컬럼 | 제약 | 설명 |
|------|------|--------|------|------|
| id | Long | id | PK | |
| memberSubmitProblem | FK | member_submit_problem_id | NOT NULL | 소속 제출 |
| status | Enum(MemberTempCodeStatusEnum) | status | NOT NULL | TEST / CORRECT / INCORRECT |
| code | TEXT | code | | 저장된 코드 |
| submitDate | LocalDateTime | submit_date | DEFAULT now | 저장 시각 |

### 4.7 Room (WebSocket 도움방)

| 필드 | 타입 | 설명 |
|------|------|------|
| id | String(UUID) | 방 ID |
| title | String | “문제//언어//Owner” |
| owner | VO OwnerDto | 방장 |
| memberRoles | Map<String, RoomRole> | 이메일 → 권한 |

**권한 규칙:**
- CHAT_ONLY: 채팅만
- CHAT_AND_EDIT: 채팅 + 코드편집 (방장/지정 유저)

---

## 5. Enum 요약

| Enum | 값 |
|------|-----|
| MemberRoleEnum | USER, ADMIN |
| Permission | PROBLEM_CREATE ~ ADMIN_DELETE |
| ProblemLanguageEnum | JAVA(62), PYTHON(71), C(50) |
| MemberTempCodeStatusEnum | TEST, CORRECT, INCORRECT |
| RoomRole | CHAT_ONLY, CHAT_AND_EDIT |

---

## 6. 핵심 비즈니스 규칙

| ID | 규칙 |
|----|------|
| BR-P01 | 한 회원은 문제당 1개의 pass 제출만 보존 |
| BR-P02 | 임시코드는 회원·문제별 가장 최근 1건 노출 |
| BR-SEC01 | REST + WebSocket 모두 JWT Bearer 검증 필수 |
| BR-ROOM01 | 방장 퇴장 시 Room 삭제 & 참여자 알림 |
| BR-ROLE01 | ADMIN만 ProblemCode CRUD, 문제 삭제 가능 |

---

## 7. Auditing & Soft-Delete

- 모든 Entity → BaseEntity 상속 (created_at, updated_at)
- is_deleted = true + deleted_at 시 논리 삭제
- Repository 레이어 조회 시 is_deleted = false 조건 필수

---

## 8. 인덱스 & 성능

- 단일: `idx_email`, `idx_problem_code`, `idx_created_at`
- 복합:
    - `idx_member_pass (member_id, pass)` – 통계
    - `idx_room_member (room_id, member_email)` – 실시간 방 권한
- QueryDSL + `@EntityGraph` 로 N+1 최소화
- HikariCP – 20 커넥션, 배치 size 50

---

## 9. 테스트 전략

- Entity 단위: 불변/비즈니스 메서드 검증
- Repository: 필터·페이징 쿼리 결과 검증
- Service: CompileService → Judge0 Mock 으로 TC 시뮬레이션
- WebSocket: STOMP integration test (Spring Messaging Test)

---

## 10. 체크리스트

- [ ] 모든 FK에 인덱스가 존재하는가?
- [ ] Soft-Delete 컬럼이 빠지지 않았는가?
- [ ] Auditing 컬럼 자동 세팅 확인했는가?
- [ ] JWT & Permission AOP 적용 대상 누락 없는가?

---

## 11. 마무리

- Problem Domain·Member Domain를 중심으로 핵심 엔티티 설계를 완료.
- 향후 Code 실행 로그, Ranking, Badge 등 확장 엔티티는 동일 규칙으로 추가 예정.
