# 📚 Readlog - 나만의 독서 기록 서비스

> 읽은 책을 기록하고 관리하는 개인 독서 관리 플랫폼

## 🎯 프로젝트 소개

Readlog는 사용자가 읽은 책을 체계적으로 기록하고 관리할 수 있는 웹 서비스입니다.
- 네이버 책 검색 API를 통한 간편한 책 검색
- 평점, 독서 기간, 후기 등 상세한 독서 기록
- 사용자별 독립적인 책장 관리
- 네이버 소셜 로그인을 통한 간편 인증

## 🛠️ 기술 스택

### Backend
- **Language**: Kotlin 1.9.20
- **Framework**: Spring Boot 3.2.x
- **Architecture**: Hexagonal Architecture (Clean Architecture)
- **Security**: Spring Security + OAuth2 + JWT
- **Database**: H2 (개발) / PostgreSQL (운영)
- **ORM**: Spring Data JPA
- **Build Tool**: Gradle (Kotlin DSL)

### Frontend
- **Framework**: React 18 + Vite
- **Language**: JavaScript
- **Styling**: Tailwind CSS
- **Routing**: React Router v6
- **HTTP Client**: Fetch API

### External API
- 네이버 책 검색 API
- 네이버 OAuth2 로그인

## 🏗️ 아키텍처

### 헥사고날 아키텍처 (Hexagonal Architecture)

```
📦 Domain Layer (순수 비즈니스 로직)
   ├── model/
   │   ├── User
   │   ├── Book
   │   ├── ReadingRecord
   │   └── Value Objects (Rating, ReadingPeriod)
   └── exception/

📦 Application Layer (Use Cases)
   ├── port/
   │   ├── input/  (Use Case 인터페이스)
   │   └── output/ (Repository 인터페이스)
   └── service/
       ├── ReadingRecordService
       ├── BookService
       └── dto/

📦 Infrastructure Layer (기술 구현)
   ├── persistence/
   │   ├── entity/ (JPA Entity)
   │   ├── repository/ (Spring Data JPA)
   │   └── adapter/ (Repository 구현체)
   ├── security/
   │   ├── JwtUtil
   │   ├── OAuth2SuccessHandler
   │   └── JwtAuthenticationFilter
   └── external/
       └── naver/ (네이버 API 연동)

📦 Adapter Layer (외부 인터페이스)
   └── web/
       ├── controller/
       └── dto/
```

### 주요 패턴
- **Port & Adapter Pattern**: 의존성 역전을 통한 계층 분리
- **Value Object Pattern**: 도메인 규칙 캡슐화 (Rating, ReadingPeriod)
- **Repository Pattern**: 영속성 계층 추상화
- **TDD (Test-Driven Development)**: 테스트 주도 개발

## 🚀 시작하기

### 필수 요구사항
- JDK 17 이상
- Node.js 20.x 이상
- 네이버 개발자 센터 애플리케이션 등록

### 1. 네이버 API 설정

1. [네이버 개발자 센터](https://developers.naver.com/apps/#/register) 접속
2. 애플리케이션 등록
   - 사용 API: **검색**, **네이버 로그인**
   - 서비스 URL: `http://localhost:8080`
   - Callback URL: `http://localhost:8080/login/oauth2/code/naver`
3. Client ID, Client Secret 발급

### 2. 백엔드 설정

```bash
# 프로젝트 클론
git clone https://github.com/your-username/readlog.git
cd readlog/backend

# 환경 변수 설정 (IntelliJ Run Configuration 또는 환경 변수)
NAVER_API_CLIENT_ID=your_client_id
NAVER_API_CLIENT_SECRET=your_client_secret

# 또는 application-local.yml 생성 (Git 무시)
# src/main/resources/application-local.yml
spring:
  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: your_client_id
            client-secret: your_client_secret

naver:
  api:
    client-id: your_client_id
    client-secret: your_client_secret

# 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

백엔드 서버: http://localhost:8080

### 3. 프론트엔드 설정

```bash
cd ../frontend

# 의존성 설치
npm install

# 개발 서버 실행
npm run dev
```

프론트엔드 서버: http://localhost:5173

## 📖 주요 기능

### 1. 인증 (Authentication)
- 네이버 소셜 로그인
- JWT 기반 토큰 인증
- 자동 로그아웃 (토큰 만료 시)

### 2. 책 검색
- 네이버 책 검색 API 연동
- 실시간 검색 결과
- 책 상세 정보 (제목, 저자, 출판사, 설명, 이미지)

### 3. 독서 기록 관리
- **생성**: 책 선택 → 평점/날짜/후기 입력 → 저장
- **조회**: 
  - 전체 목록
  - 상태별 필터 (읽는 중/완독/중단)
  - 정렬 (최근 읽은 순/평점 순/제목 순/작가 순)
- **수정**: 평점, 날짜, 후기, 상태 수정
- **삭제**: 독서 기록 삭제

### 4. 개인 책장
- 사용자별 독립적인 책장
- 3단 그리드 레이아웃 (반응형)
- 통계 표시 (전체/완독/읽는중 권수)

### 5. 상세 페이지
- 책 정보 및 독서 기록 상세 보기
- 인라인 수정 모드
- 삭제 기능

## 🎨 UI/UX 특징

- **파스텔 하늘색 테마**: 일관된 색상 시스템
- **반응형 디자인**: 모바일/태블릿/데스크톱 대응
- **토스트 알림**: 작업 성공/실패 피드백
- **부드러운 애니메이션**: 페이지 전환 및 요소 등장 효과
- **직관적인 네비게이션**: 명확한 페이지 구조
- 
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

### 프론트엔드
```bash
npm run test
```

## 📂 프로젝트 구조

```
readlog/
├── backend/
│   └── src/
│       ├── main/
│       │   ├── kotlin/com/booktracker/
│       │   │   ├── domain/
│       │   │   ├── application/
│       │   │   ├── infrastructure/
│       │   │   └── adapter/
│       │   └── resources/
│       │       └── application.yml
│       └── test/
└── frontend/
    ├── src/
    │   ├── App.jsx
    │   ├── api.js
    │   ├── LoginPage.jsx
    │   ├── AuthCallbackPage.jsx
    │   ├── BookshelfPage.jsx
    │   ├── BookSearchApp.jsx
    │   ├── BookDetailPage.jsx
    │   ├── ReadingRecordModal.jsx
    │   └── Toast.jsx
    └── package.json
```

## 🔒 보안

- JWT 기반 Stateless 인증
- CORS 설정으로 출처 제한
- 사용자별 데이터 격리 (userId 기반)
- 권한 체크 (본인 데이터만 접근 가능)
- 민감 정보 환경 변수 관리

## 🚧 향후 개선 사항

- [ ] 다른 사용자 리뷰 공개 기능
- [ ] 책 추천 시스템
- [ ] 독서 통계 및 리포트
- [ ] 독서 목표 설정
- [ ] 책갈피 기능
- [ ] 소셜 공유 기능
- [ ] PostgreSQL 마이그레이션
- [ ] Docker 배포 환경 구성

## 📝 라이선스

MIT License

## 👤 개발자

- GitHub: [@yesjm-dev]
- Email: yesjm.dev@gmail.com