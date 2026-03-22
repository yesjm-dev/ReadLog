import React from 'react';
import { ArrowLeft, LogOut } from 'lucide-react';

function SettingsPage({ onBack, onLogout }) {
  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50">
      {/* 헤더 */}
      <div className="bg-amber-50 shadow-sm px-4 py-3 flex items-center gap-3 border-b border-amber-200">
        <button onClick={onBack} className="p-2 hover:bg-amber-100 rounded-full transition-all">
          <ArrowLeft className="w-5 h-5 text-amber-800" />
        </button>
        <h1 className="text-lg font-bold text-stone-800">설정</h1>
      </div>

      <div className="max-w-lg mx-auto p-4 mt-4">
        <button
          onClick={onLogout}
          className="w-full px-6 py-4 bg-white border-2 border-red-200 text-red-500 rounded-2xl font-bold transition-all hover:bg-red-50 flex items-center justify-center gap-2 shadow-sm"
        >
          <LogOut className="w-5 h-5" />
          로그아웃
        </button>
      </div>
    </div>
  );
}

export default SettingsPage;
