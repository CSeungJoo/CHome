# CHome - IoT 스마트홈 플랫폼

## 📚 프로젝트 소개

CHome은 IoT 디바이스를 실시간으로 제어하고 모니터링할 수 있는 스마트홈 플랫폼입니다. MQTT 프로토콜 기반의 양방향 통신과 헥사고날 아키텍처를 적용하여, 확장 가능하고 유지보수가 용이한 IoT 백엔드 시스템을 구축했습니다.

ESP32 기반의 IoT 허브, Spring Boot 백엔드, React 프론트엔드까지 End-to-End로 직접 설계 및 구현하였으며, AWS 클라우드 인프라 위에서 안정적으로 운영되도록 하는 것을 목표로 합니다.

1. **MVP 구축**
    - 헥사고날 아키텍처(Port/Adapter) 기반으로 도메인 모듈(Auth, User, Hub, Device)을 분리하여 구현했습니다.
    - MQTT 양방향 통신과 SSE 기반 실시간 디바이스 상태 푸시를 구축했습니다.
    - JWT + Redis Refresh Token 기반 인증, 이메일 인증 플로우를 도입했습니다.
    - Mosquitto MQTT 브로커가 백엔드 API로 인증/ACL을 위임하는 구조를 적용했습니다.
2. **안정성 및 운영성 개선**
    - MQTT 커맨드에 Redis 기반 멱등성 처리를 적용하여 중복 명령 문제를 해결했습니다.


<br>

## 🛠 기술 스택

<img width="983" height="544" alt="Image" src="https://github.com/user-attachments/assets/02646ba1-74f6-41f5-8f41-fe59c931ed60" />

<br>
<br>

## 📌 주요 기능

- **IoT 허브/디바이스 관리**: 허브 등록, 디바이스 조회, 별칭 변경, 명령 전송
- **실시간 디바이스 제어**: MQTT를 통한 허브 명령 전송 및 SSE를 통한 실시간 결과 수신
- **인증/인가**: JWT 기반 인증 + Redis Refresh Token + 이메일 인증
- **권한 관리**: 허브/디바이스별 사용자 권한 (READ/UPDATE/DELETE)
- **MQTT 브로커 인증 연동**: Mosquitto 플러그인이 백엔드 API로 인증/ACL 검증
- **반응형 웹 UI**: 모바일/데스크톱 대응, 다크모드 지원

<br>
<br>

## 🚀 기술적 도전 과제 및 개선 사항

### 1. Hub 목록 조회 API 성능 개선

- **문제**: EXISTS 서브쿼리 사용 시 FK 기본 인덱스(hub_id)만으로는 
  서브쿼리 실행마다 평균 684개의 불필요한 행을 읽으며 9,975번 반복 실행되어 
  p95 응답시간 59.15s, K6 부하 테스트 기준 727개 요청 실패 발생
- **해결 과정**:
    - (1) EXPLAIN ANALYZE로 병목 구간 특정 (actual time=4684ms)
    - (2) 카디널리티가 높은 user_id를 선두 컬럼으로 복합 인덱스(user_id, hub_id) 설계
    - (3) Covering Index 효과로 테이블 접근 없이 인덱스만으로 조건 해결
- **결과**: 서브쿼리 1회당 읽는 행 684 → 3.82로 감소, p95 응답시간 59.15s → 34.51ms (99.9% 개선), 전체 요청 성공

<br>

### 2. MQTT 커맨드 멱등성 처리

- **문제**: MQTT는 QoS 1/2 환경에서도 네트워크 재시도 및 재연결로 인해 동일한 커맨드 메시지가 중복 전달될 수 있음. 디바이스 제어 명령(예: 도어락 열기, 조명 On/Off)이 중복 처리되면 사용자 경험과 디바이스 상태에 직접적인 부작용 발생.
- **해결 과정**:
    - (1) 모든 커맨드에 고유한 `commandId`(UUID)를 부여하도록 메시지 스키마 정의
    - (2) Redis에 `commandId` 키를 TTL과 함께 저장 (`SETNX` 기반 원자 연산)
    - (3) 이미 처리된 `commandId`로 들어온 요청은 소비 단계에서 무시
    - (4) 처리 결과는 별도 토픽으로 발행하여 SSE를 통해 클라이언트에게 즉시 전달
- **결과**: 동일 커맨드가 중복으로 전달되어도 디바이스 측면에서는 정확히 한 번만 실행됨을 보장. 네트워크 불안정 환경에서도 안정적인 제어 흐름 확보.

<br>

### 3. 헥사고날 아키텍처 기반 도메인 모듈 분리

- **문제**: 초기 레이어드 아키텍처에서 도메인 로직과 인프라(Web, MQTT, JPA, Redis)가 강하게 결합되어, 신규 기능 추가 시 변경 범위가 광범위해지고 테스트가 어려워짐.
- **해결 과정**:
    - `port/in`(Use Case 인터페이스) / `port/out`(외부 시스템 추상화) / `adapter`(웹·MQTT·영속성·보안) 구조로 분리
    - 도메인 모듈을 Auth / User / Hub / Device / Shared 로 분리
    - Application 서비스는 오직 Port에만 의존하여 인프라 교체에 대한 영향 최소화
- **결과**: 도메인 로직과 인프라 어댑터의 독립적 변경 가능. Use Case 단위 단위 테스트 작성이 용이해짐.

<br>

### 4. 실시간 디바이스 상태 푸시 (SSE)

- **문제**: 기존 폴링 기반으로 디바이스 상태를 가져올 경우 응답 지연과 불필요한 트래픽 증가가 발생.
- **해결 과정**:
    - WebSocket 대신 SSE를 채택 (단방향 푸시·HTTP 기반·ALB 호환·인프라 단순성)
    - 사용자별 SSE 커넥션을 관리하고, MQTT 결과 메시지를 사용자–허브 권한에 따라 라우팅
    - 커넥션 타임아웃 및 재연결 시 누락 방지를 위한 heartbeat 적용
- **결과**: 디바이스 명령 결과가 평균 수백 ms 이내로 사용자 화면에 반영됨.

<br>
<br>

## 📂 프로젝트 구조

```
CHome/
├── CHome_BE/                              # Backend (Spring Boot)
│   ├── src/main/java/kr/cseungjoo/chome_be/
│   │   ├── auth/                          # 인증 모듈
│   │   │   ├── adapter/web/               # REST Controller
│   │   │   ├── application/service/       # Use Case 구현
│   │   │   ├── domain/                    # 도메인 예외
│   │   │   └── port/in/                   # Inbound Port
│   │   ├── user/                          # 사용자 모듈
│   │   ├── hub/                           # 허브 모듈
│   │   ├── device/                        # 디바이스 모듈
│   │   └── shared/                        # 공통 모듈
│   │       ├── adapter/mqtt/              # MQTT 통신
│   │       ├── adapter/security/          # JWT, Redis 토큰
│   │       ├── adapter/web/sse/           # SSE 실시간 푸시
│   │       └── port/out/                  # Outbound Port
│   ├── Dockerfile                         # Multi-stage build (JDK 21)
│   └── docker-compose.yml                 # 로컬 개발 환경
│
├── chome-fe/                              # Frontend (React + TS)
│
└── hub/                                   # ESP32 IoT Hub 펌웨어 (C++)
```

<br>
<br>

## 🌐 서비스 URL

| 서비스 | URL |
|--------|-----|
| CHome API | https://api-chome.cseungjoo.kr |
| CHome Web | https://chome.cseungjoo.kr |
