import React, { useEffect, useRef } from 'react';
import { useToast } from './Toast';

function AuthCallbackPage({ onLoginSuccess }) {
  const { showToast } = useToast();
  const executedRef = useRef(false); // StrictMode에서도 중복 방지

  useEffect(() => {
    if (executedRef.current) return; // 이미 실행됐다면 무시
    executedRef.current = true;

    const params = new URLSearchParams(window.location.search);
    const token = params.get('token');

    if (token) {
      // 토큰 저장
      localStorage.setItem('accessToken', token);

      // 토스트 (한 번만 실행)
      showToast('로그인 성공! 🎉', 'success');

      // 1초 후 책장 페이지로 이동
      const timer = setTimeout(() => {
        onLoginSuccess();
      }, 1000);

      return () => clearTimeout(timer);
    } else {
      // 실패 처리
      showToast('로그인 실패. 다시 시도해주세요.', 'error');

      // 토큰 제거 후 2초 뒤 로그인 페이지로
      const timer = setTimeout(() => {
        localStorage.removeItem('accessToken');
        onLoginSuccess();
      }, 2000);

      return () => clearTimeout(timer);
    }
  }, [onLoginSuccess, showToast]);

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50 flex items-center justify-center">
      <div className="text-center">
        <div className="inline-block w-16 h-16 border-4 border-sky-400 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p className="text-sky-700 font-medium text-lg">로그인 처리 중...</p>
      </div>
    </div>
  );
}

export default AuthCallbackPage;