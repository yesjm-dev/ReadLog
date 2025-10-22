// App.jsx
import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate } from 'react-router-dom'
import BookSearchApp from './BookSearchApp'
import BookshelfPage from './BookshelfPage'
import BookDetailPage from './BookDetailPage'
import LoginPage from './LoginPage'
import AuthCallbackPage from './AuthCallbackPage'
import { ToastProvider } from './Toast'

function AppContent() {
  const [selectedRecordId, setSelectedRecordId] = useState(null)
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const navigate = useNavigate()

  // 로그인 상태 확인
  useEffect(() => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      setIsLoggedIn(true)
    }
  }, [])

  const handleGoToDetail = (recordId) => {
    setSelectedRecordId(recordId)
    navigate('/detail')
  }

  const handleLoginSuccess = () => {
    const token = localStorage.getItem('accessToken')
    if (token) {
      setIsLoggedIn(true)
      navigate('/')
    } else {
      navigate('/login')
    }
  }

  const handleLogout = () => {
    localStorage.removeItem('accessToken')
    setIsLoggedIn(false)
    navigate('/login')
  }

  // 로그인 안되어있으면 로그인 페이지로
  if (!isLoggedIn) {
    return (
      <Routes>
        <Route path="/auth/callback" element={<AuthCallbackPage onLoginSuccess={handleLoginSuccess} />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    )
  }

  return (
    <Routes>
      <Route path="/" element={
        <BookshelfPage 
          onAddBook={() => navigate('/search')}
          onBookClick={handleGoToDetail}
          onLogout={handleLogout}
        />
      } />
      
      <Route path="/search" element={
        <BookSearchApp 
          onGoToBookshelf={() => navigate('/')} 
        />
      } />

      <Route path="/detail" element={
        <BookDetailPage
          recordId={selectedRecordId}
          onBack={() => navigate('/')}
          onDelete={() => navigate('/')}
        />
      } />

      <Route path="*" element={<Navigate to="/" />} />
    </Routes>
  )
}

function App() {
  return (
    <BrowserRouter>
      <ToastProvider>
        <AppContent />
      </ToastProvider>
    </BrowserRouter>
  )
}

export default App