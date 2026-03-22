import { useState, useEffect } from 'react'
import { BrowserRouter, Routes, Route, Navigate, useNavigate, useLocation } from 'react-router-dom'
import { BookOpen, Search, MessageCircle } from 'lucide-react'
import BookSearchApp from './BookSearchApp'
import BookshelfPage from './BookshelfPage'
import BookDetailPage from './BookDetailPage'
import LoginPage from './LoginPage'
import AuthCallbackPage from './AuthCallbackPage'
import ChatPage from './ChatPage'
import ChatListPage from './ChatListPage'
import SettingsPage from './SettingsPage'
import { ToastProvider } from './Toast'

function BottomNav() {
  const navigate = useNavigate()
  const location = useLocation()

  const tabs = [
    { path: '/', icon: BookOpen, label: '책장' },
    { path: '/search', icon: Search, label: '검색' },
    { path: '/chats', icon: MessageCircle, label: 'AI 채팅' },
  ]

  const hiddenPaths = ['/chat', '/detail', '/settings']
  if (hiddenPaths.includes(location.pathname)) return null

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 bg-amber-50 border-t border-amber-200 shadow-lg">
      <div className="flex justify-around items-center h-16 max-w-lg mx-auto">
        {tabs.map(({ path, icon: Icon, label }) => {
          const isActive = location.pathname === path
          return (
            <button
              key={path}
              onClick={() => navigate(path)}
              className={`flex flex-col items-center justify-center gap-0.5 flex-1 h-full transition-colors ${
                isActive ? 'text-amber-800' : 'text-stone-400'
              }`}
            >
              <Icon className="w-5 h-5" />
              <span className="text-[10px] font-medium">{label}</span>
            </button>
          )
        })}
      </div>
    </div>
  )
}

function AppContent() {
  const [selectedRecordId, setSelectedRecordId] = useState(null)
  const [selectedChatRoomId, setSelectedChatRoomId] = useState(null)
  const [selectedBookForChat, setSelectedBookForChat] = useState(null)
  const [isLoggedIn, setIsLoggedIn] = useState(false)
  const navigate = useNavigate()

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

  if (!isLoggedIn) {
    return (
      <Routes>
        <Route path="/auth/callback" element={<AuthCallbackPage onLoginSuccess={handleLoginSuccess} />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
    )
  }

  const handleGoToChat = (chatRoomId) => {
    setSelectedChatRoomId(chatRoomId)
    setSelectedBookForChat(null)
    navigate('/chat')
  }

  const handleGoToNewChat = (book) => {
    setSelectedBookForChat(book)
    setSelectedChatRoomId(null)
    navigate('/chat')
  }

  const location = useLocation()
  const hiddenPaths = ['/chat', '/detail', '/settings']
  const showNav = !hiddenPaths.includes(location.pathname)

  return (
    <div className={showNav ? 'pb-16' : ''}>
      <Routes>
        <Route path="/" element={
          <BookshelfPage onBookClick={handleGoToDetail} />
        } />

        <Route path="/search" element={
          <BookSearchApp onGoToNewChat={handleGoToNewChat} />
        } />

        <Route path="/detail" element={
          <BookDetailPage
            recordId={selectedRecordId}
            onBack={() => navigate('/')}
            onDelete={() => navigate('/')}
            onGoToChat={handleGoToChat}
            onGoToNewChat={handleGoToNewChat}
          />
        } />

        <Route path="/chats" element={
          <ChatListPage onChatClick={handleGoToChat} />
        } />

        <Route path="/chat" element={
          <ChatPage
            chatRoomId={selectedChatRoomId}
            book={selectedBookForChat}
            onBack={() => navigate(-1)}
            onChatRoomCreated={(id) => setSelectedChatRoomId(id)}
          />
        } />

        <Route path="/settings" element={
          <SettingsPage
            onBack={() => navigate('/')}
            onLogout={handleLogout}
          />
        } />

        <Route path="*" element={<Navigate to="/" />} />
      </Routes>
      <BottomNav />
    </div>
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
