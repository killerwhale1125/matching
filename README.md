# 토스페이먼츠를 통한 결제 연동 시스템 및 프로필 조회수 증가 캐싱 안전성 확보

## 프로젝트 개요

- 토스페이먼츠 결제 API 연동 처리 과정에서 발생하는 다양한 시나리오의 안전성 확보
- 결제 승인, 검증, 재시도 가능한 오류 처리와 결제 상태 관리를 통해 시스템 안전성 확보
- 프로필 상세 조회 시 추가적인 조회수 증가 I/O 성능 저하 및 동시성 안전 확보

<br>

## 해결 과제
- 소프트웨어 아키텍쳐 설계 : 도메인의 결합도 및 의존성을 감소를 위한 아키텍쳐 설계
- 결제 데이터 정합성 : 결제 요청, 승인 과정에서 데이터 불일치 및 무결성 깨짐을 방지하기 위한 데이터 검증
- 조회수 증가의 동시성 및 동기화 : InMemory 캐싱 시 발생할 수 있는 조회수 동시성 문제 및 데이터 일관성 확보
- API 응답 지연 및 서버 다운 : 스케줄링 데이터 복구 로직을 통해 항상 일관되고, 안정적인 결제 처리 및 조회수 증가 설계

<br>

## 사용 기술

| 사용 기술 | 버전                                      | 사용 목적                          |
|:--------|:---------------------------------------|-------------------------------|
| Java   | 21                  | 경량 가상 스레드 기반으로 자원 효율성과 동시성 처리 성능에 유리 |
| SpringBoot | 3.5.3 | DI, AOP, AutoConfiguration 등으로 생산성과 유연한 의존성 관리 |
| MySQL | 8.0.36 | MySQL 안정화 LTS 버전이며, 결제 등 중요한 작업 시 UNDO 로그 기반 트랜잭션으로 읽기-쓰기 일관성 보장 및 안정적인 데이터 처리  |
| Redis | 7.4.2 | 조회수 증가 로직의 세밀한 캐싱 제어 및 AOF/RDB 기반 데이터 유실 복구 스케줄링 가능 |
| Spring Data JPA | 3.2.3 | 엔티티 중심의 비즈니스 로직 구현을 통해 도메인 주도 개발 구조 정립 |
| Querydsl   | 5.0.0 | 동적 쿼리 간편 구현 및 MyBatis 대비 IDE, 컴파일 시점 오류 파악 가능 |

<br>

## 추가 라이브러리
- Lombok : 반복되는 코드 제거로 간결화
- Spring Validation : 요청 값 유효성 검증 처리 및 코드 정리
- Httpclient5 : RestClient 기반의 HTTP 통신에서 커넥션 제어 및 타임아웃 설정을 위해 사용

<br>

## 구성 API


| 기능 | URL                                       | 메소드 | 설명                          |
|:--------|:---------------------------------------|--------|-------------------------------|
| 회원가입   | /api/members/signup                  | POST   | 간단한 회원가입 |
| 회원 프로필 상세 조회   | /api/profiles/{profileId}| GET    | 회원 ID로 프로필 조회 및 조회수 증가 |
| 회원 프로필 리스트 조회 | /api/profiles            | GET    | 필터링 조건에 부합하는 회원 목록 조회 |
| Toss 결제 정보 생성   | /api/payments/toss/checkout | POST | Toss 결제 전 사용자 결제 정보 생성 및 응답 |
| Toss 결제 승인 요청   | /api/payments/toss/confirm | POST | Toss 결제 승인 전 사용자 검증 및 결제 상태 처리 후 사용자 포인트 충전 |

<br>

## 프로젝트 실행법
1. 이 저장소를 클론하거나 `.zip` 파일로 다운로드한 후 압축을 풉니다.
3. 프로젝트 루트 디렉토리에서 아래 명령어를 실행합니다.

<br>

```bash
docker compose up       // foreground
docker compose up -d    // background
```

<br>

3. 초기 빌드가 완료되면 다음과 같은 서비스들이 실행됩니다.
- Redis Port 6380 [Docker 8080]
- MySQL Port 3307 [Docker 3306]
- SpringBoot Port 8081 [Docker 8080]

<br>

> ⚠️ application.yml 등의 환경 설정 파일이 필요하다면, 사전에 적절히 구성해야 합니다. (현재 프로젝트는 자동 구성 되어있음)

<br>

4. 도커 정상적 실행 확인 

```bash
docker ps
docker logs {container-id}
```

5. API 테스트 용 회원 생성 

url : /api/members/signup

> ⚠️ 회원 생성 API는 1번만 호출해주세요! 단순 생성 구현으로 중복 검증 로직이 포함되어있지 않습니다.

### ✅ RequestBody
```json
{
    "name": "아무 이름이나 넣어주세요."
}
```

### ✅ ResponseBody
```json
{
    "status": 200,
    "message": "요청에 성공하였습니다.",
    "code": "SUCCESS",
    "result": {
        "name": "아무 이름이나 넣어주세요.",
        "profileInfo": {
            "viewCount": 0
        },
        "pointInfo": {
            "point": 0
        }
    }
}
```

<br>

6. DB 데이터 생성 확인

```bash
docker exec -it mysql mysql -umatching_user -p
password : matching_pass

> use matching;
> select * from member \G;
```

<br>

## 패키지 구조 및 의존성

![image](https://github.com/user-attachments/assets/eabf7d5e-f91f-4204-b0b1-0d4bdd5e7f57)

```text
├── application                     // application : 비즈니스 로직을 처리하는 계층
│   ├── TossPaymentConfirmServiceImpl.java
│   ├── dto
│   ├── port
│   │   ├── TossPaymentRepository.java
│   └── usecase                     // usecase : 애플리케이션의 특정 비즈니스 흐름 처리 전용  
│       └── TossPaymentExecutorUseCase.java
├── domain                          // domain : 핵심 비즈니스 로직의 순수 도메인 
│   └── TossPayment.java
├── exception
├── infrastructure                  // infrastructure : 외부 시스템과 상호작용하는 구현체 
│   ├── entity                      // entity: DB와 매핑되는 데이터베이스 테이블을 나타내는 클래스 ex ) JPA Entity
│   │   └── TossPaymentEntity.java
│   ├── internal                    // internal : 서버 내부 도메인과 통신하기 위한 구현체 
│   │   └── InternalMemberProvider.java
│   └── repostitory                 // repostitory : 데이터베이스 접근 시 인터페이스 및 구현체
│       ├── TossPaymentJpaRepository.java 
│       └── TossPaymentRepositoryImpl.java
└── presentation                    // presentation : 외부 요청을 받아들이고 응답을 반환
    ├── TossPaymentController.java
    ├── dto
    └── port
        └── TossPaymentConfirmService.java
```

<br>

# 프로필 상세 조회 시 조회수 증가 API 

url : @GET /api/profiles/{profileId}
param :
- Long profileId

### 🎯 핵심 문제 정의
- 실시간으로 DB에 조회수를 반영하면 DB 부하가 증가함
- 캐싱되지 않은 상태에서 여러 사용자가 동시에 조회수를 캐싱하려 할 경우 동시성 이슈 발생
- 자정 무렵 Redis ↔ DB 동기화 중 조회 요청 발생 시 일시적인 조회수 불일치 가능성
- Redis 장애 시 조회수 반영 누락 발생 가능

<br>

### ❌ 초기 구현 방식의 한계

1. DB에서 +1 처리 후, 일정 기준(예: 조회수 % 10 == 0) 도달 시 Hot 유저 등록 및 캐싱 시작
2. 캐싱 이후 조회는 Redis에서 처리
3. 스케줄링 시 하루치 일정 기준 조회수 미달 시 Hot 유저 탈락하여 조회수 캐싱이 삭제되는 방식

<br>

**해당 방식 문제점**
- **데이터 일관성 저하** : 캐싱 직전 동시에 다수의 조회 요청 처리가 반영되지 않아 실제보다 낮은 값으로 Redis에 저장됨
- **메모리 낭비** : 캐싱 시 하나의 프로필에 대해 3가지 (Hot유저 일치 유무, 캐싱된 날짜, 조회수) Redis Key가 삽입되어 Redis 메모리 낭비 발생
- **동기화 충돌** : 스케줄링 중 요청 발생 시 데이터 불일치 가능 

<br>

### ✅ 개선된 조회수 증가 처리 흐름

![image](https://github.com/user-attachments/assets/e745e45e-3b24-4d57-a24e-cf5902e705aa)

1. 클라이언트가 프로필 상세 조회 요청 (GET /api/profiles/{profileId})
2. Redis에서 오늘 날짜 기준의 조회수 키 존재 여부 확인
   - 존재할 경우 → INCR를 통해 원자적 증가
   - 존재하지 않을 경우
     - 어제 날짜 Redis 키 또는 DB 조회수를 기준으로 + 1 처리 후 캐싱 시도
     - 중복 삽입 방지를 위해 setIfAbsent 수행
       - 실패 시 이미 누군가 캐싱한 의미로 응답 반환 시 Redis INCR 후 반환 값을 응답으로 사용 ( 동시성 제어 )
3. Redis 장애 발생 시 → 조회수 누락 이력을 별도 테이블에 기록 (markAsLoss)
4. 최종 조회수를 반영한 Profile 반환
5. 추후 새벽 1시 사용자가 없는 시간대에 어제 날짜의 캐시된 조회수를 DB에 반영 및 실패 시 ProfileHistory 저장

<br>

✅ 요약: 프로필이 조회될 때 마다 profileId + yyyy-MM-dd 정보로 캐싱되며, 해당 정보로 캐시 유무를 판단하여 증가 처리. 하루가 지나면 스케줄링을 통해 Redis → DB로 동기화됩니다.

<br>

### 🧠 설계 및 구현 핵심 키워드
- Redis 기반 조회수 캐싱 처리
- 날짜별 Redis 키 관리 (profile:view:{id}:{yyyyMMdd} 형식)
- setIfAbsent + INCR 조합으로 동시 접근 시 중복 캐싱 방지 및 조회수 일관성 확보
- Redis 장애 발생 시 조회수 누락 History 기록 및 DB와 동기화하는 스케줄링 처리

<br>

### ✅ 문제 해결 방법
- **데이터 일관성 확보** : 조회수는 DB와 분리하여 Redis로만 처리하고, setIfAbsent + INCR 조합으로 동시성 제어 및 일관성 확보
- **메모리 사용률 단축** : 프로필당 1개 키(profile:view:{id}:{yyyyMMdd})만 저장하여 날짜를 기반으로 스케줄링과 조회의 안전성을 확보 
- **예외 처리** : Redis 작업 예외 발생 시 외부 예외로 해당 조회의 손실을 기록 

<br>

⚠️ DB 동기화 스케줄링 및 예외처리는 구현하였지만 Redis 장애 시 조회수 손실 복구 스케줄러는 시간상의 이유로 구현하지 못했습니다.

<br>

### ⏱ 조회수 동기화 스케줄러

![image](https://github.com/user-attachments/assets/db0b411a-879f-4483-bd2f-6a72f338b361)


실행 시점: 매일 새벽 1시 (사용자 적은 시간대)

프로세스
1. 어제 날짜의 Redis 조회수 키 일괄 조회
2. 해당 키값을 DB에 덮어쓰기
   - 프로필 조회 요청이 발생 시 요청 사용자는 어제 날짜 캐싱값이 Redis에 남아있다면 그 값을 사용하여 신규 캐싱하며, 아닐 경우 DB에서 조회한 값 (스케줄링 완료 시점) 을 사용하기 때문에 동시성이 제어됨
4. 예외 발생 시 → 조회수 손실 기록 (lossMap) 및 DB History 저장

<br>

### ✨ 개선 방향 및 남은 과업

현재 구현한 방식이 소규모 서비스에서는 프로필 조회마다 캐시하여 저장이 효율적이라 생각하지만, 서비스 확장 시 하루 몇만명의 프로필 조회가 발생할 경우 Redis 메모리 낭비에 대해 추가적인 개선이 필요할 것 같습니다.

<br>
<br>
<br>
<br>

# 프로필 목록 조회 API

url : @GET /api/profiles

### 🎯 핵심 문제 정의
- 목록 조회 시 DB <-> Cache 조회수 미동기화로 인한 정렬 문제

<br>

### 💡 문제제 해결 아이디어
사용자 및 전문가 매칭 서비스 특성 상 사용자가 특정 전문가를 직접 검색하기보다는 주제에 맞는 다양한 전문가 목록을 탐색하는 방식에 가깝습니다.

<br>

이에 따라,
- 기본적으로는 DB 조회 시 최신 등록순(LATEST) Index 기반 페이지네이션된 프로필을 제공하고,
- 사용자가 원하는 경우 해당 페이지 내에서만 조회수순, 이름 가나다순 등으로 정렬할 수 있도록 설계하였습니다.

<br>

### 🔹 이 방식의 장점
- **조회수 캐싱 구조와 충돌 없음** : 조회수는 Redis에만 캐싱되므로, DB와 실시간 동기화되지 않아도 정렬 기능에 영향 없음
- **단일 Index 효율 증가** :
   - 정렬 조건이 DB 쿼리에 포함되지 않기 때문에, 복합 인덱스나 정렬 전용 컬럼 관리 불필요
   -  결과적으로 INSERT/UPDATE/DELETE 시 Index 관리 오버헤드 감소
- **정렬의 유연성 확보** : 조회수, 이름순 등 사용자 정의 정렬을 애플리케이션 레벨에서 처리 가능

<br>

### 🔁 개선된 조회수 증가 처리 흐름

✅ Redis 캐싱, 날짜별 키 구성, setIfAbsent + INCR 조합을 통한 동시성 제어 및 조회수 일관성 확보

![image](https://github.com/user-attachments/assets/d84cd190-e622-4342-bd6d-03ed7b87062f)

<br>

### 🧠 설계 및 구현 핵심 키워드

**효율적인 캐시 접근**
- 각 프로필별 Redis 키를 개별 조회하지 않고, **bulk 조회 (multiGet)**로 처리하여 I/O 및 네트워크 비용을 O(N) → O(1) 수준으로 최적화
**단일 키 기반 캐싱 전략**
- profile:view:{id}:{yyyyMMdd} 형식으로 키를 구성하여 메모리 절약 및 스케줄링 동기화 시점 명확화
**애플리케이션 단 정렬 처리**
- 정렬 조건이 DB에 영향을 주지 않아, 빠른 응답성과 다양한 정렬 기준을 유연하게 처리 가능

<br>
<br>
<br>
<br>

# 결제 정보 생성 API 

url : @POST /api/payments/toss/checkout

### 🧠 설계 및 구현 핵심 키워드
**결제 상태 분리**
- 결제 요청 시 생성되는 정보를 Event History에 저장하여, 실제 결제 승인과 분리된 독립적인 상태 추적 가능
**유연한 확장성 확보**
- Event 기반 구조를 통해, 결제 승인 요청 또는 실패 시 재시도 로직 처리 시에도 기존 요청의 상태 이력을 기반으로 명확한 판단 및 재처리 흐름 제어 가능

<br>

### ✅ RequestBody
```json
{
    "amount" : 50000,
    "orderName" : "포인트 충전",
    "memberId" : 1
}
```

<br>

### ✅ ResponseBody
```json
{
    "status": 200,
    "message": "요청에 성공하였습니다.",
    "code": "SUCCESS",
    "result": {
        "orderId": "adb12d7c-c493-4ef4-ad27-3aa5390dd824",
        "amount": 50000,
        "orderName": "포인트 충전",
        "memberId": 1,
        "successUrl": "http://127.0.0.1/success",
        "failUrl": "http://127.0.0.1/fail",
        "clientKey": "test_gck_docs_Ovk5rk1EwkEbP0W43n07xlzm"
    }
}
```

<br>
<br>
<br>
<br>

# 결제 승인 API 

url : @POST /api/payments/toss/confirm

### 🎯 핵심 문제 정의
- 결제 승인 전 결제 이벤트 정보를 기반으로 상태 값을 정확하게 처리해야 하는 문제
- Toss 전용 결제 도메인의 로직이 과도하게 퍼져 있어, 응집력 저하 및 결합도 증가 문제
- 결제 실패 발생 시 사용자 요청에 대해 재시도 가능한 구조로 처리하는 흐름 제어 문제
- 결제 중복 요청 시 멱등성 문제
- 예외 발생 시 트랜잭션 범위를 명확히 지정하여 데이터 정합성과 롤백 안정성을 보장하는 문제

<br>

### ✅ RequestBody
```json
{
    "memberId" : 1,
    "paymentKey" : "tgen_20250703215445yds20",
    "orderId" : "40a7e1d8-eeca-4ee7-9309-fdd70fd20016",
    "amount" : 5000
}
```

<br>

### ❌ 초기 구현 방식과 한계

![image](https://github.com/user-attachments/assets/d1e126fc-f78c-41c5-9f82-74fb00055b8d)

1. 사용자가 결제 승인 요청 시 해당 정보를 DB에서 검증 후 Toss에 승인 요청
2. 성공시 결제 정보 DB 저장, 실패 시 결제 실패 처리

<br>

**해당 방식 문제점**
1. **결제 승인 중 발생한 에러에 대한 명확한 처리 부재** : 재시도를 통해 해결될 수 있는 에러도 실패로 간주 
2. **API 지연으로 인한 결제 처리 오류** : Toss 및 서버 측 응답 지연으로 Toss는 승인 완료했지만, 포인트는 충전되지 않는 문제 
3. **결제 승인 요청 중 서버 중단** : Toss 측 승인 완료 시점 백엔드 서버가 다운되어 값을 반환받지 못해 포인트 충전이 불가능한 상황

<br>

### ✅ 개선된 결제 승인 처리 흐름

![image](https://github.com/user-attachments/assets/737de2e6-277f-41c0-94f4-09ac28640719)

<br>

### ✅ 문제 해결 과정

결제 시나리오는 아래와 같은 복잡하고 다양한 예외 상황이 존재합니다.

- 이미 결제에 성공했지만 서버 장애로 인해 충전이 누락되는 경우
- 결제 재요청 시 중복 승인 또는 잘못된 상태 변경
- Toss 측 결제는 완료되었지만 내부 비즈니스 처리가 중단되는 경우 등

이처럼 하나하나의 예외를 직접 핸들링하는 것은 유지보수성과 안정성 측면에서 매우 비효율적이라 판단하였습니다.
따라서 문제의 복잡도를 낮추기 위해, 다음과 같은 상태 기반 처리 전략을 설계하였습니다:

<br>

1. **⚙️ 안전한 구간과 위험 구간 분리**
   - Toss API 요청/응답을 외부 통신이 포함된 위험 구간으로 정의하고 별도 트랜잭션으로 분리
   - 요청 검증 → 결제 이벤트 저장까지는 내부 안전한 영역에서 처리
   - 결제 승인 → 포인트 충전은 외부 실패 가능성을 고려하여 별도 트랜잭션에서 처리

> 트랜잭션 분리를 통해 외부 실패에 의한 전체 실패를 방지하고, 재시도 가능한 구조로 구성

2. **🧩 Toss 응답 코드 기반 상태 분기 처리**
   - Toss 측 응답의 code, message 규칙을 정리하여 예측 가능한 상태값(EventStatus) 으로 변환
   - Toss 응답 결과에 따라 결제 이벤트 객체의 상태를 기록하고 이후 처리를 분기
   - 네트워크 지연, 서버 다운 등의 실패 원인 파악이 어려운 상황(UNKNOWN) 도 명확히 구분

3. **🧱 도메인 중심 상태 변경 캡슐화**
   - 상태 변경은 오직 결제 도메인 객체 내부 메서드를 통해 수행되도록 설계
   - 서비스/비즈니스 레이어에서는 직접 상태값 변경을 금지하여 무결성 보장
   - 상태 기반의 로직 분기를 도메인 내부에 숨겨 캡슐화와 책임 분리 강화

4. **🧱 멱등성(Idempotency) 검증 처리**
   - 각 결제 요청 시 고유한 멱등키를 DB에 저장하고, 같은 결제에 대해 다시 요청하면 해당 키로 중복 결제 방지
   - 해당 paymentKey가 결제 완료 상태로 응답받았지만 포인트 충전이 안되있다면 결제 Event 상태에 따라 스케줄링으로 복구 가능

<br>

### ⏱ 결제 재시도 전략 및 스케줄러

![image](https://github.com/user-attachments/assets/186667bd-0e65-45ed-8b4d-342fba5385a5)


우선적으로 복잡한 재시도 로직을 단순화하기 위해, 재시도 가능한 상태를 아래 두 가지로 한정하였습니다.
- UNKOWN : 알수 없음 ( 서버 지연, 중단 등 )
- IN_PROGRESS : 결제는 시작되었지만 승인되지 않은 상태

두 상태를 재시도 가능으로 선정한다면, 나머지 상태에 대한 복잡성은 크게 감소합니다.
따라서 Toss 측 승인 요청 시 두 상태를 기준으로 예외 발생 시 결제 Event 내역을 철저히 기록하였으며,
재시도 스케줄링을 통해 복구를 실행하되, 실행 가능 횟수도 지정하여 최종적으로 가능 횟수를 초과할 경우
결제 실패 처리 되도록 설계하였습니다.

<br>

✅ 예외 시나리오별 처리 전략 예시

| 상황 | 대응 전략                                   | 비고 |
|:----------------------------|:------------------------------------------------------------------------------------------|--------------------------------------------|
| Toss 승인 완료 후 서버 다운   | 상태 값은 IN_PROGRESS, UNKNOWN 둘중 하나인 상태이기에 재시도 스케줄링으로 복구 가능            | 멱등키로 중복 방지                          |
| Toss 응답 실패 (타임아웃)     | 상태값을 UNKNOWN으로 저장하여 재시도 가능하게 함                                             | RestClient 내부 .onStatus() 에서 예외 캐치  |
| 포인트 충전 중 예외 발생      | 트랜잭션 분리로 앞 단계(결제 승인)는 유지, 이후 실패 기록 및 재시도 스케줄링                    | 충전 누락 방지                              | 
| 동일 결제 재시도 요청         | DB에 저장된 paymentKey와 결제 상태를 기반으로 중복 여부 판단                                  | 재시도 횟수 초과 시 FAILED 처리              | 

여기까지 읽어주셔서 감사드립니다.
