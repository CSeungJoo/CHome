# CHome API 분석 문서

## 작성일: 2026-03-23

---

## 1. API 개요

- **Base URL**: `http://localhost:8080/api/v1`
- **인증 방식**: Bearer Token (JWT)
- **응답 공통 구조**:
  ```json
  { "status": "SUCCESS" | "ERROR", "data": { ... } }
  ```

---

## 2. Postman 응답 확인된 API (8개)

| # | 이름 | Method | Endpoint | Status |
|---|------|--------|----------|--------|
| 1 | 회원가입 | POST | /users | 201 |
| 2 | 이메일 인증 | GET | /users/verify?token= | 200 |
| 3 | 로그인 | POST | /auth/login | 200 |
| 4 | 토큰 연장 | POST | /auth/refresh | 200 |
| 5 | 허브 등록 | POST | /hubs | 200 |
| 6 | 허브 조회 | GET | /hubs?page= | 200 |
| 7 | 허브 삭제 | DELETE | /hubs/{hubId} | 200 |
| 8 | 허브 별명 변경 | PUT | /hubs/{hubId} | 200 |

## 3. Postman 응답 없는 API - 백엔드 컨트롤러 기반 (5개)

| # | 이름 | Method | Endpoint |
|---|------|--------|----------|
| 1 | SSE 연결 | GET | /hubs/{hubId}/sse |
| 2 | 명령어 수행 | POST | /hubs/{hubId}/command |
| 3 | 디바이스 조회 | GET | /devices?hubId= |
| 4 | 디바이스 단일 조회 | GET | /devices/{deviceId} |
| 5 | 디바이스 별명 변경 | PUT | /devices/{deviceId} |
