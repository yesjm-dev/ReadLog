import React from 'react';
import { BookOpen, LogIn } from 'lucide-react';

function LoginPage() {
  const handleNaverLogin = () => {
    // 백엔드 OAuth2 엔드포인트로 리다이렉트
    window.location.href = 'http://localhost:8080/oauth2/authorization/naver';
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50 flex items-center justify-center p-4">
      <div className="max-w-md w-full">
        
        {/* 로고 & 타이틀 */}
        <div className="text-center mb-8">
          <div className="inline-flex items-center justify-center w-20 h-20 bg-sky-400 rounded-3xl shadow-xl mb-6">
            <BookOpen className="w-12 h-12 text-white" />
          </div>
          <h1 className="text-4xl font-bold text-sky-900 mb-2">
            Readlog
          </h1>
          <p className="text-sky-600 text-lg">
            나만의 독서 기록 서비스
          </p>
        </div>

        {/* 로그인 카드 */}
        <div className="bg-white rounded-3xl shadow-2xl p-8 sm:p-10">
          <h2 className="text-2xl font-bold text-sky-900 text-center mb-8">
            시작하기
          </h2>

          {/* 네이버 로그인 버튼 */}
          <button
            onClick={handleNaverLogin}
            className="w-full bg-[#03C75A] hover:bg-[#02B350] text-white font-bold py-4 px-6 rounded-2xl transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-3 mb-6"
          >
            <svg className="w-6 h-6" viewBox="0 0 24 24" fill="currentColor">
              <path d="M16.273 12.845L7.376 0H0v24h7.726V11.156L16.624 24H24V0h-7.727v12.845z"/>
            </svg>
            네이버로 시작하기
          </button>

          {/* 안내 문구 */}
          <p className="text-center text-sm text-sky-600">
            로그인하면 독서 기록을 저장하고<br />
            다양한 기능을 이용할 수 있어요
          </p>
        </div>

        {/* 푸터 */}
        <div className="text-center mt-8 text-sky-500 text-sm">
          <p>📚 읽은 책을 기록하고 공유하세요</p>
        </div>
      </div>
    </div>
  );
}

export default LoginPage;