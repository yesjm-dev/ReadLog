# 📚 Readlog - 나만의 독서 기록 서비스

> 읽은 책을 기록하고 관리하는 개인 독서 관리 플랫폼

## 🎯 프로젝트 소개

Readlog는 사용자가 읽은 책을 체계적으로 기록하고 관리할 수 있는 웹 서비스입니다.
- 네이버 책 검색 API를 통한 간편한 책 검색
- 평점, 독서 기간, 후기 등 상세한 독서 기록
- 사용자별 독립적인 책장 관리
- 네이버 소셜 로그인을 통한 간편 인증
- AI 독서 추천 채팅 (Gemini 연동)

## 🛠️ 기술 스택

### Backend
- **Language**: Kotlin 1.9.25
- **Framework**: Spring Boot 3.5.x
- **Architecture**: Hexagonal Architecture (Clean Architecture)
- **AI**: Spring AI + Google Gemini
- **Security**: Spring Security + OAuth2 + JWT
- **Database**: PostgreSQL (Docker)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle (Kotlin DSL)

### Frontend
- **Framework**: React 19 + Vite
- **Language**: JavaScript
- **Styling**: Tailwind CSS
- **Routing**: React Router v7
- **HTTP Client**: Axios
- **Fonts**: Gaegu (로고), Gowun Batang (리뷰)

### External API
- 네이버 책 검색 API
- 네이버 OAuth2 로그인
- Google Gemini AI (Spring AI)

## 🏗️ 아키텍처

### 헥사고날 아키텍처 (Hexagonal Architecture)

```
📦 Domain Layer (순수 비즈니스 로직)
   ├── model/
   │   ├── User, Book, ReadingRecord
   │   ├── ChatRoom, ChatMessage
   │   └── Value Objects (Rating, ReadingPeriod)
   └── exception/

📦 Application Layer (Use Cases)
   ├── port/
   │   ├── input/  (Use Case 인터페이스)
   │   └── output/ (Repository, AI 인터페이스)
   └── service/
       ├── ReadingRecordService
       ├── BookService
       ├── ChatService
       └── dto/

📦 Infrastructure Layer (기술 구현)
   ├── persistence/ (JPA Entity, Repository, Adapter)
   ├── security/ (JWT, OAuth2)
   └── external/
       ├── naver/ (네이버 API 연동)
       └── gemini/ (Gemini AI 연동)

📦 Adapter Layer (외부 인터페이스)
   └── web/
       ├── controller/
       └── dto/
```

### 주요 패턴
- **Port & Adapter Pattern**: 의존성 역전을 통한 계층 분리
- **Value Object Pattern**: 도메인 규칙 캡슐화 (Rating, ReadingPeriod)
- **Repository Pattern**: 영속성 계층 추상화

## 🚀 시작하기

### 필수 요구사항
- JDK 17 이상
- Node.js 20.x 이상
- Docker (PostgreSQL 실행용)
- 네이버 개발자 센터 애플리케이션 등록
- Google AI Studio API Key (Gemini)

### 1. Docker (PostgreSQL)

```bash
docker compose up -d
```

### 2. 백엔드 설정

```bash
cd readlog

# 환경 변수 설정
NAVER_API_CLIENT_ID=your_client_id
NAVER_API_CLIENT_SECRET=your_client_secret
JWT_SECRET=your_jwt_secret
GEMINI_API_KEY=your_gemini_api_key

# 실행
./gradlew bootRun
```

백엔드 서버: http://localhost:8080

### 3. 프론트엔드 설정

```bash
cd readlog-frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드 서버: http://localhost:5173

## 📖 주요 기능

### 1. 인증
- 네이버 소셜 로그인
- JWT 기반 토큰 인증

### 2. 책 검색
- 네이버 책 검색 API 연동
- 추천 키워드 (에세이, 소설, 자기계발 등)
- 검색 결과 캐시 유지 (탭 전환 시에도)

### 3. 독서 기록 관리
- 책 선택 → 상태/평점/날짜/후기 입력 → 저장
- 정렬 (최근 읽은 순/평점 순/제목 순/작가 순)
- 완독 처리 (원터치로 오늘 날짜 완독)
- 평점은 완독 시에만 필수

### 4. 개인 책장
- 3단 그리드 레이아웃 (반응형)
- 하단 네비게이션 바 (책장/검색/AI 채팅)
- 설정 페이지 (로그아웃)

### 5. 상세 페이지
- 책 정보 및 독서 기록 상세
- 읽은 기간 표시 ("15일간 읽었어요")
- 책 소개 (네이버 API description)
- AI와 대화하기 바로가기
- 인라인 수정/삭제 (커스텀 확인 모달)

### 6. AI 독서 추천 채팅
- 책별 AI 채팅방 (Gemini 연동)
- 첫 메시지 전송 시 채팅방 자동 생성
- 추천 질문 선택지 (첫 진입 시)
- 독서 기록 기반 맞춤 추천
- 대화 요약을 통한 토큰 관리

## 🎨 UI/UX 특징

- **웜 베이지 테마**: amber/stone 계열 서재 느낌
- **감성 타이포그래피**: Gaegu (로고), Gowun Batang (리뷰)
- **반응형 디자인**: 모바일 우선 설계
- **하단 네비게이션**: 책장/검색/AI 채팅 3탭
- **커스텀 모달**: 삭제 확인 등 시스템 팝업 대체
- **토스트 알림**: 작업 성공/실패 피드백
- **부드러운 애니메이션**: fadeInUp 등장 효과

## 🧪 테스트

### 백엔드
```bash
./gradlew test
```

테스트 커버리지:
- Domain Layer: 단위 테스트
- Application Layer: UseCase 테스트
- Infrastructure Layer: Repository 통합 테스트
- Adapter Layer: Controller 테스트 (@WebMvcTest)

## 📂 프로젝트 구조

```
readlog/
├── readlog/                 # Backend (Spring Boot)
│   └── src/
│       ├── main/kotlin/com/yesjm/readlog/
│       │   ├── domain/
│       │   ├── application/
│       │   ├── infrastructure/
│       │   └── adapter/
│       └── resources/
│           └── application.yml
├── readlog-frontend/        # Frontend (React)
│   ├── src/
│   │   ├── App.jsx
│   │   ├── BookshelfPage.jsx
│   │   ├── BookSearchApp.jsx
│   │   ├── BookDetailPage.jsx
│   │   ├── ChatPage.jsx
│   │   ├── ChatListPage.jsx
│   │   ├── SettingsPage.jsx
│   │   ├── ReadingRecordModal.jsx
│   │   ├── ConfirmModal.jsx
│   │   └── Toast.jsx
│   └── package.json
└── docker-compose.yml
```

## 🔒 보안

- JWT 기반 Stateless 인증
- CORS 설정으로 출처 제한
- 사용자별 데이터 격리 (userId 기반)
- 민감 정보 환경 변수 관리

## 🚧 향후 개선 사항

- [ ] 독서 통계 및 리포트
- [ ] 독서 목표 설정
- [ ] 인상 깊은 구절 메모 기능
- [ ] Docker 배포 환경 구성

## 📝 라이선스

MIT License

## 👤 개발자

- GitHub: [@yesjm-dev]
- Email: yesjm.dev@gmail.com
