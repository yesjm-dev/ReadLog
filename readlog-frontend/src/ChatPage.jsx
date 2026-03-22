import React, { useState, useEffect, useRef } from 'react';
import { ArrowLeft, Send, Bot, User, Trash2 } from 'lucide-react';
import { api } from './api';

function ChatPage({ chatRoomId, onBack }) {
  const [chatRoom, setChatRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(true);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (chatRoomId) {
      fetchChatRoom();
    }
  }, [chatRoomId]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const fetchChatRoom = async () => {
    setLoading(true);
    try {
      const data = await api.get(`/api/chat-rooms/${chatRoomId}`);
      setChatRoom(data);
      setMessages(data.messages || []);
    } catch (err) {
      setError('채팅방을 불러올 수 없습니다');
    } finally {
      setLoading(false);
    }
  };

  const handleSend = async () => {
    if (!input.trim() || sending) return;

    const userMessage = input.trim();
    setInput('');
    setSending(true);

    // 낙관적 업데이트: 사용자 메시지 먼저 표시
    const tempUserMsg = {
      id: Date.now(),
      chatRoomId,
      role: 'USER',
      content: userMessage,
      createdAt: new Date().toISOString()
    };
    setMessages(prev => [...prev, tempUserMsg]);

    try {
      const response = await api.post(`/api/chat-rooms/${chatRoomId}/messages`, {
        content: userMessage
      });
      // AI 응답 추가
      setMessages(prev => [...prev, response]);
    } catch (err) {
      setError('메시지 전송에 실패했습니다');
      // 실패 시 사용자 메시지 제거
      setMessages(prev => prev.filter(m => m.id !== tempUserMsg.id));
      setInput(userMessage);
    } finally {
      setSending(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('이 채팅방을 삭제하시겠습니까?')) return;
    try {
      await api.delete(`/api/chat-rooms/${chatRoomId}`);
      onBack();
    } catch (err) {
      setError('채팅방 삭제에 실패했습니다');
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50 flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block w-12 h-12 border-4 border-sky-400 border-t-transparent rounded-full animate-spin"></div>
          <p className="mt-4 text-sky-700 font-medium">불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50 flex flex-col">
      {/* 헤더 */}
      <div className="bg-white shadow-md px-4 py-3 flex items-center gap-3">
        <button onClick={onBack} className="p-2 hover:bg-sky-50 rounded-xl transition-all">
          <ArrowLeft className="w-5 h-5 text-sky-700" />
        </button>
        {chatRoom?.bookImageUrl && (
          <img src={chatRoom.bookImageUrl} alt="" className="w-10 h-13 object-cover rounded-lg" />
        )}
        <div className="flex-1 min-w-0">
          <h1 className="font-bold text-sky-900 truncate">{chatRoom?.bookTitle || '채팅'}</h1>
          <p className="text-xs text-sky-500">AI 독서 추천</p>
        </div>
        <button onClick={handleDelete} className="p-2 hover:bg-red-50 rounded-xl transition-all">
          <Trash2 className="w-5 h-5 text-red-400" />
        </button>
      </div>

      {/* 에러 */}
      {error && (
        <div className="mx-4 mt-2 p-3 bg-red-50 border border-red-200 rounded-xl text-red-700 text-sm">
          {error}
          <button onClick={() => setError('')} className="ml-2 font-bold">X</button>
        </div>
      )}

      {/* 메시지 영역 */}
      <div className="flex-1 overflow-y-auto px-4 py-4 space-y-4">
        {messages.length === 0 && (
          <div className="text-center py-12">
            <Bot className="w-16 h-16 mx-auto text-sky-300 mb-4" />
            <h3 className="text-lg font-bold text-sky-900 mb-2">AI 독서 도우미</h3>
            <p className="text-sky-600">이 책에 대해 궁금한 것을 물어보세요!</p>
            <p className="text-sky-500 text-sm mt-1">독서 기록을 바탕으로 추천해드릴게요</p>
          </div>
        )}

        {messages.map((msg) => (
          <MessageBubble key={msg.id} message={msg} />
        ))}

        {sending && (
          <div className="flex items-start gap-2">
            <div className="w-8 h-8 rounded-full bg-sky-400 flex items-center justify-center flex-shrink-0">
              <Bot className="w-5 h-5 text-white" />
            </div>
            <div className="bg-white rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm">
              <div className="flex gap-1">
                <div className="w-2 h-2 bg-sky-400 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></div>
                <div className="w-2 h-2 bg-sky-400 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></div>
                <div className="w-2 h-2 bg-sky-400 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></div>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* 입력 영역 */}
      <div className="bg-white border-t border-sky-100 px-4 py-3">
        <div className="flex gap-2 max-w-4xl mx-auto">
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="메시지를 입력하세요..."
            rows={1}
            className="flex-1 px-4 py-3 rounded-2xl border-2 border-sky-200 focus:border-sky-400 focus:outline-none resize-none"
            disabled={sending}
          />
          <button
            onClick={handleSend}
            disabled={!input.trim() || sending}
            className="px-4 py-3 bg-sky-400 hover:bg-sky-500 text-white rounded-2xl transition-all disabled:bg-gray-300 disabled:cursor-not-allowed"
          >
            <Send className="w-5 h-5" />
          </button>
        </div>
      </div>
    </div>
  );
}

function MessageBubble({ message }) {
  const isUser = message.role === 'USER';

  return (
    <div className={`flex items-start gap-2 ${isUser ? 'flex-row-reverse' : ''}`}>
      <div className={`w-8 h-8 rounded-full flex items-center justify-center flex-shrink-0 ${
        isUser ? 'bg-indigo-400' : 'bg-sky-400'
      }`}>
        {isUser ? (
          <User className="w-5 h-5 text-white" />
        ) : (
          <Bot className="w-5 h-5 text-white" />
        )}
      </div>
      <div className={`max-w-[75%] rounded-2xl px-4 py-3 shadow-sm ${
        isUser
          ? 'bg-indigo-400 text-white rounded-tr-sm'
          : 'bg-white text-sky-900 rounded-tl-sm'
      }`}>
        <p className="whitespace-pre-wrap text-sm leading-relaxed">{message.content}</p>
      </div>
    </div>
  );
}

export default ChatPage;
