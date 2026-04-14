# CHome Frontend 작업 로그

## 2026-03-23

### 1차: 프로젝트 분석 및 초기 구축

#### 수행한 행동

1. **Postman 컬렉션 분석**
   - CHome.postman_collection.json 전체 분석
   - 총 13개 API 식별 (응답 있음 8개, 응답 없음 5개)
   - 응답 없는 API는 백엔드 컨트롤러 소스코드를 직접 분석하여 Request/Response 타입 추정

2. **백엔드 컨트롤러 분석**
   - UserController, AuthController, MqttAuthController, SseController
   - HubController, DeviceController
   - Hexagonal Architecture (Port & Adapter) 패턴 확인
   - 모든 DTO (Request/Response) 클래스 확인

3. **프론트엔드 아키텍처 설계**
   - Feature-Sliced Design (FSD) 아키텍처 채택
   - 유지보수 용이성: 기능별 독립 모듈 → 기능 추가/수정 시 영향 범위 최소화
   - 단방향 의존성: app → features → shared

4. **Next.js 프로젝트 생성 및 설정**
   - Next.js 15 + TypeScript + TailwindCSS 4
   - 추가 패키지: axios, @tanstack/react-query, zustand, react-hook-form, zod, react-icons

5. **공통 모듈 구현 (shared/)**
   - `shared/api/client.ts`: Axios 인스턴스 + 토큰 자동 주입 인터셉터 + 401 자동 토큰 갱신
   - `shared/types/api.ts`: BaseResponse, BaseError, MsgResponse 공통 타입
   - `shared/ui/`: Button, Input, Modal, Card, Sidebar 컴포넌트
   - `stores/auth-store.ts`: Zustand 인증 상태 관리 (hydrate 패턴)

6. **Feature 모듈 구현**
   - `features/auth/`: types, api (login, register, verifyEmail), hooks (useLogin, useRegister)
   - `features/hub/`: types, api (CRUD + SSE + command), hooks (useHubs, useRegisterHub, useDeleteHub, useChangeHubAlias, useSendHubCommand), ui (RegisterHubModal, EditHubAliasModal)
   - `features/device/`: types, api (getDevices, getDeviceDetail, changeDeviceAlias), hooks (useDevices, useDeviceDetail, useChangeDeviceAlias)

7. **페이지 구현**
   - `/login`: 이메일/비밀번호 로그인 (zod 유효성 검증)
   - `/register`: 회원가입 폼
   - `/dashboard`: 허브 수 요약 카드 + 허브 목록
   - `/hubs`: 허브 CRUD (등록 모달, 별명 변경 모달, 삭제 확인)
   - `/hubs/[hubId]`: 허브 상세 (디바이스 목록 + SSE 실시간 로그 + BLE 스캔 명령)
   - `/devices`: 허브 선택 → 디바이스 목록으로 이동
   - `/devices/[deviceId]`: 디바이스 상세 (기본 정보 + 명령어 목록 + 별명 변경)

#### 디렉토리 구조
```
src/
├── app/
│   ├── (auth)/login/page.tsx
│   ├── (auth)/register/page.tsx
│   ├── (auth)/layout.tsx
│   ├── (main)/dashboard/page.tsx
│   ├── (main)/hubs/page.tsx
│   ├── (main)/hubs/[hubId]/page.tsx
│   ├── (main)/devices/page.tsx
│   ├── (main)/devices/[deviceId]/page.tsx
│   ├── (main)/layout.tsx
│   ├── providers.tsx
│   ├── layout.tsx
│   └── page.tsx
├── features/
│   ├── auth/{api,hooks,types}/
│   ├── hub/{api,hooks,types,ui}/
│   └── device/{api,hooks,types}/
├── shared/
│   ├── api/client.ts
│   ├── types/api.ts
│   └── ui/{button,input,modal,card,sidebar}.tsx
└── stores/auth-store.ts
```

---

### 2차: Jenkinsfile 및 CI/CD 구성

#### 수행한 행동

1. **BE Jenkinsfile 분석**
   - 기존 BE 파이프라인 구조 확인: Checkout → Build → Docker Build & Push → Update Deploy Manifest
   - 레지스트리: `localhost:30500` (로컬 프라이빗 레지스트리)
   - 이미지 태그: `BUILD_NUMBER-GIT_COMMIT_SHORT` 포맷
   - 배포: `cseungjoo-deploy` 깃 저장소의 K8s manifest 업데이트 방식 (GitOps)

2. **FE Dockerfile 작성 (멀티스테이지 빌드)**
   - Stage 1 (build): `node:22-alpine` → `npm ci` → `npm run build`
   - Stage 2 (runtime): `node:22-alpine` → standalone 서버만 복사
   - `next.config.ts`에 `output: "standalone"` 설정 추가 (Docker 최적화)

3. **FE Jenkinsfile 작성**
   - BE와 동일한 파이프라인 구조 적용
   - `IMAGE_NAME`: `chome-fe`
   - Manifest 경로: `manifests/projects/chome-fe/deployment.yaml`
   - Test stage는 TODO로 주석 처리 (BE와 동일 패턴)

#### 생성된 파일
- `chome-fe/Dockerfile` - 멀티스테이지 Docker 빌드
- `chome-fe/Jenkinsfile` - CI/CD 파이프라인
- `chome-fe/next.config.ts` - standalone 출력 설정 추가
