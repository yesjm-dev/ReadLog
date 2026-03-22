import React, { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { ArrowLeft, Send, Bot, User, Trash2 } from 'lucide-react';
import { api } from './api';
import ConfirmModal from './ConfirmModal';
import { invalidateChatListCache } from './ChatListPage';

function ChatPage({ chatRoomId, book, onBack, onChatRoomCreated }) {
  const navigate = useNavigate();
  const [currentRoomId, setCurrentRoomId] = useState(chatRoomId);
  const [chatRoom, setChatRoom] = useState(null);
  const [messages, setMessages] = useState([]);
  const [input, setInput] = useState('');
  const [loading, setLoading] = useState(!!chatRoomId);
  const [sending, setSending] = useState(false);
  const [error, setError] = useState('');
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const messagesEndRef = useRef(null);

  const isNewChat = !chatRoomId && book;

  useEffect(() => {
    if (chatRoomId) {
      fetchChatRoom();
    } else if (!book) {
      navigate('/chats');
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

  const handleSend = async (directMessage) => {
    const userMessage = (directMessage || input).trim();
    if (!userMessage || sending) return;

    setInput('');
    setSending(true);

    const tempUserMsg = {
      id: Date.now(),
      role: 'USER',
      content: userMessage,
      createdAt: new Date().toISOString()
    };
    setMessages(prev => [...prev, tempUserMsg]);

    try {
      let roomId = currentRoomId;

      // 새 채팅: 책 저장 → 채팅방 생성
      if (!roomId && book) {
        const savedBook = await api.post('/api/books', {
          title: book.title,
          author: book.author,
          isbn: book.isbn,
          imageUrl: book.imageUrl,
          publisher: book.publisher,
          description: book.description
        });
        const chatRoomData = await api.post('/api/chat-rooms', { bookId: savedBook.id });
        roomId = chatRoomData.id;
        setCurrentRoomId(roomId);
        setChatRoom(chatRoomData);
        if (onChatRoomCreated) onChatRoomCreated(roomId);
        invalidateChatListCache();
      }

      const response = await api.post(`/api/chat-rooms/${roomId}/messages`, {
        content: userMessage
      });
      setMessages(prev => [...prev, response]);
    } catch (err) {
      setError('메시지 전송에 실패했습니다');
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
    try {
      await api.delete(`/api/chat-rooms/${chatRoomId}`);
      invalidateChatListCache();
      navigate('/chats');
    } catch (err) {
      setError('채팅방 삭제에 실패했습니다');
    } finally {
      setShowDeleteConfirm(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50 flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block w-12 h-12 border-4 border-amber-600 border-t-transparent rounded-full animate-spin"></div>
          <p className="mt-4 text-stone-700 font-medium">불러오는 중...</p>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50 flex flex-col">
      {/* 헤더 */}
      <div className="bg-amber-50 shadow-sm px-4 py-3 flex items-center gap-3 border-b border-amber-200">
        <button onClick={() => navigate(-1)} className="p-2 hover:bg-amber-100 rounded-xl transition-all">
          <ArrowLeft className="w-5 h-5 text-amber-800" />
        </button>
        {(chatRoom?.bookImageUrl || book?.imageUrl) && (
          <img src={chatRoom?.bookImageUrl || book?.imageUrl} alt="" className="w-10 h-13 object-cover rounded-lg" />
        )}
        <div className="flex-1 min-w-0">
          <h1 className="font-bold text-stone-800 truncate">{chatRoom?.bookTitle || book?.title || '채팅'}</h1>
          <p className="text-xs text-amber-600">AI 독서 추천</p>
        </div>
        {currentRoomId && (
          <button onClick={() => setShowDeleteConfirm(true)} className="p-2 hover:bg-red-50 rounded-xl transition-all">
            <Trash2 className="w-5 h-5 text-red-400" />
          </button>
        )}
      </div>

      <ConfirmModal
        isOpen={showDeleteConfirm}
        title="채팅방 삭제"
        message="이 대화를 삭제하면 되돌릴 수 없어요. 정말 삭제할까요?"
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteConfirm(false)}
      />

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
            <Bot className="w-16 h-16 mx-auto text-amber-300 mb-4" />
            <h3 className="text-lg font-bold text-stone-900 mb-2">AI 독서 도우미</h3>
            <p className="text-stone-600 mb-6">이 책에 대해 궁금한 것을 물어보세요!</p>
            <div className="flex flex-wrap justify-center gap-2 max-w-md mx-auto">
              {[
                '이 책은 어떤 내용이야?',
                '내 취향에 맞을까?',
                '비슷한 책 추천해줘',
                '이 책의 핵심 메시지는?',
              ].map((suggestion) => (
                <button
                  key={suggestion}
                  onClick={() => handleSend(suggestion)}
                  className="px-4 py-2 bg-white border-2 border-amber-200 text-stone-700 rounded-full text-sm hover:bg-amber-50 hover:border-amber-300 transition-all shadow-sm"
                >
                  {suggestion}
                </button>
              ))}
            </div>
          </div>
        )}

        {messages.map((msg) => (
          <MessageBubble key={msg.id} message={msg} />
        ))}

        {sending && (
          <div className="flex items-start gap-2">
            <div className="w-8 h-8 rounded-full bg-amber-600 flex items-center justify-center flex-shrink-0">
              <Bot className="w-5 h-5 text-white" />
            </div>
            <div className="bg-white rounded-2xl rounded-tl-sm px-4 py-3 shadow-sm">
              <div className="flex gap-1">
                <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce" style={{ animationDelay: '0ms' }}></div>
                <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce" style={{ animationDelay: '150ms' }}></div>
                <div className="w-2 h-2 bg-amber-600 rounded-full animate-bounce" style={{ animationDelay: '300ms' }}></div>
              </div>
            </div>
          </div>
        )}

        <div ref={messagesEndRef} />
      </div>

      {/* 입력 영역 */}
      <div className="bg-white border-t border-stone-200 px-4 py-3">
        <div className="flex gap-2 max-w-4xl mx-auto">
          <textarea
            value={input}
            onChange={(e) => setInput(e.target.value)}
            onKeyDown={handleKeyPress}
            placeholder="메시지를 입력하세요..."
            rows={1}
            className="flex-1 px-4 py-3 rounded-2xl border-2 border-amber-200 focus:border-amber-600 focus:outline-none resize-none"
            disabled={sending}
          />
          <button
            onClick={handleSend}
            disabled={!input.trim() || sending}
            className="px-4 py-3 bg-amber-600 hover:bg-amber-700 text-white rounded-2xl transition-all disabled:bg-gray-300 disabled:cursor-not-allowed"
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
        isUser ? 'bg-amber-700' : 'bg-amber-600'
      }`}>
        {isUser ? (
          <User className="w-5 h-5 text-white" />
        ) : (
          <Bot className="w-5 h-5 text-white" />
        )}
      </div>
      <div className={`max-w-[75%] rounded-2xl px-4 py-3 shadow-sm ${
        isUser
          ? 'bg-amber-700 text-white rounded-tr-sm'
          : 'bg-white text-stone-900 rounded-tl-sm'
      }`}>
        <p className="whitespace-pre-wrap text-sm leading-relaxed">{message.content}</p>
      </div>
    </div>
  );
}

export default ChatPage;
