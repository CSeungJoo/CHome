# CHome Frontend 아키텍처 설계서

## 작성일: 2026-03-23

## 기술 스택
- Next.js 15 (App Router) + React 19 + TailwindCSS 4
- Zustand (상태관리) + TanStack Query (서버 상태)
- Axios (HTTP) + React Hook Form + Zod (폼/검증)

## 아키텍처: Feature-Sliced Design (FSD)

```
src/
├── app/              # Next.js App Router
│   ├── (auth)/       # 비인증 라우트 (login, register)
│   ├── (main)/       # 인증 필요 라우트 (dashboard, hubs, devices)
│   └── providers.tsx # 전역 Provider
├── features/         # 기능별 독립 모듈
│   ├── auth/         # 인증 (api, hooks, types, ui)
│   ├── hub/          # 허브 관리
│   └── device/       # 디바이스 관리
├── shared/           # 공유 모듈
│   ├── api/          # Axios 인스턴스 + 인터셉터
│   ├── types/        # 공통 타입
│   ├── ui/           # 공통 UI 컴포넌트
│   └── lib/          # 유틸리티
└── stores/           # Zustand 스토어
```

## 의존성 규칙
- `app → features → shared` (단방향)
- features 간 직접 참조 금지

## 인증 흐름
1. 로그인 → JWT + refreshToken 저장
2. Axios 인터셉터 → Bearer 자동 주입
3. 401 → refreshToken으로 재발급 → 재요청
4. refreshToken 만료 → 로그인 리다이렉트
