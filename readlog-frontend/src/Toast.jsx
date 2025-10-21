import React, { useState, useEffect } from 'react';
import { CheckCircle, XCircle, AlertCircle, X } from 'lucide-react';

// 토스트 컨텍스트와 Provider
export const ToastContext = React.createContext();

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const showToast = (message, type = 'success') => {
    const id = Date.now();
    setToasts(prev => [...prev, { id, message, type }]);
    
    // 3초 후 자동 제거
    setTimeout(() => {
      removeToast(id);
    }, 3000);
  };

  const removeToast = (id) => {
    setToasts(prev => prev.filter(toast => toast.id !== id));
  };

  return (
    <ToastContext.Provider value={{ showToast }}>
      {children}
      <ToastContainer toasts={toasts} onRemove={removeToast} />
    </ToastContext.Provider>
  );
}

// 토스트 컨테이너
function ToastContainer({ toasts, onRemove }) {
  return (
    <div className="fixed top-4 right-4 z-50 space-y-3 pointer-events-none">
      {toasts.map(toast => (
        <Toast 
          key={toast.id} 
          toast={toast} 
          onRemove={() => onRemove(toast.id)} 
        />
      ))}
    </div>
  );
}

// 개별 토스트
function Toast({ toast, onRemove }) {
  const [isExiting, setIsExiting] = useState(false);

  const handleRemove = () => {
    setIsExiting(true);
    setTimeout(onRemove, 300);
  };

  const config = {
    success: {
      icon: CheckCircle,
      bgColor: 'bg-gradient-to-r from-green-400 to-emerald-500',
      iconColor: 'text-white'
    },
    error: {
      icon: XCircle,
      bgColor: 'bg-gradient-to-r from-red-400 to-rose-500',
      iconColor: 'text-white'
    },
    warning: {
      icon: AlertCircle,
      bgColor: 'bg-gradient-to-r from-yellow-400 to-orange-500',
      iconColor: 'text-white'
    },
    info: {
      icon: AlertCircle,
      bgColor: 'bg-gradient-to-r from-sky-400 to-blue-500',
      iconColor: 'text-white'
    }
  };

  const { icon: Icon, bgColor, iconColor } = config[toast.type] || config.success;

  return (
    <div
      className={`${bgColor} text-white rounded-2xl shadow-2xl p-4 min-w-[300px] max-w-md pointer-events-auto transform transition-all duration-300 ${
        isExiting 
          ? 'translate-x-full opacity-0' 
          : 'translate-x-0 opacity-100'
      }`}
      style={{
        animation: isExiting ? 'none' : 'slideInRight 0.3s ease-out'
      }}
    >
      <div className="flex items-center gap-3">
        <Icon className={`w-6 h-6 flex-shrink-0 ${iconColor}`} />
        <p className="flex-1 font-semibold text-sm sm:text-base">
          {toast.message}
        </p>
        <button
          onClick={handleRemove}
          className="flex-shrink-0 hover:bg-white hover:bg-opacity-20 rounded-lg p-1 transition-colors"
        >
          <X className="w-5 h-5" />
        </button>
      </div>
    </div>
  );
}

// CSS 애니메이션
const style = document.createElement('style');
style.textContent = `
  @keyframes slideInRight {
    from {
      transform: translateX(100%);
      opacity: 0;
    }
    to {
      transform: translateX(0);
      opacity: 1;
    }
  }
`;
if (!document.head.querySelector('style[data-toast-animation]')) {
  style.setAttribute('data-toast-animation', 'true');
  document.head.appendChild(style);
}

// Hook for using toast
export function useToast() {
  const context = React.useContext(ToastContext);
  if (!context) {
    throw new Error('useToast must be used within ToastProvider');
  }
  return context;
}

export default ToastProvider;