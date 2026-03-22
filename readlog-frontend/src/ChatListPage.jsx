import React, { useState, useEffect } from 'react';
import { MessageCircle, BookOpen, Trash2 } from 'lucide-react';
import { api } from './api';
import ConfirmModal from './ConfirmModal';
import { formatDistanceToNow } from 'date-fns';
import { ko } from 'date-fns/locale';

function ChatListPage({ onChatClick }) {
  const [chatRooms, setChatRooms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);

  useEffect(() => {
    fetchChatRooms();
  }, []);

  const fetchChatRooms = async () => {
    setLoading(true);
    try {
      const data = await api.get('/api/chat-rooms');
      setChatRooms(data);
    } catch (err) {
      setError('채팅 목록을 불러올 수 없습니다');
    } finally {
      setLoading(false);
    }
  };

  const handleDeleteClick = (e, id) => {
    e.stopPropagation();
    setDeleteTarget(id);
  };

  const handleDeleteConfirm = async () => {
    try {
      await api.delete(`/api/chat-rooms/${deleteTarget}`);
      setChatRooms(prev => prev.filter(room => room.id !== deleteTarget));
    } catch (err) {
      setError('삭제에 실패했습니다');
    } finally {
      setDeleteTarget(null);
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50">
      <div className="max-w-3xl mx-auto p-4 sm:p-6 lg:p-8">
        {/* 헤더 */}
        <div className="mb-6">
          <h1 className="font-logo text-2xl font-bold text-amber-600 mb-1">이야기 나눈 책들</h1>
          <p className="text-amber-500 text-sm">AI와 나눈 독서 대화</p>
        </div>

        {/* 로딩 */}
        {loading && (
          <div className="text-center py-20">
            <div className="inline-block w-12 h-12 border-4 border-amber-600 border-t-transparent rounded-full animate-spin"></div>
            <p className="mt-4 text-stone-700 font-medium">불러오는 중...</p>
          </div>
        )}

        {/* 에러 */}
        {error && (
          <div className="p-4 bg-red-50 border-2 border-red-200 rounded-2xl text-red-700 mb-6">
            {error}
          </div>
        )}

        {/* 빈 상태 */}
        {!loading && !error && chatRooms.length === 0 && (
          <div className="text-center py-20 bg-white rounded-3xl shadow-lg">
            <MessageCircle className="w-24 h-24 mx-auto text-amber-200 mb-6" />
            <h3 className="text-2xl font-bold text-stone-900 mb-3">아직 대화가 없어요</h3>
            <p className="text-stone-600 text-lg">책 검색에서 AI에게 물어보기를 시작해보세요!</p>
          </div>
        )}

        {/* 채팅방 목록 */}
        {!loading && !error && chatRooms.length > 0 && (
          <div className="space-y-3">
            {chatRooms.map((room) => (
              <div
                key={room.id}
                onClick={() => onChatClick(room.id)}
                className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all cursor-pointer border-2 border-stone-200 hover:border-amber-300 p-4 flex items-center gap-4"
              >
                {room.bookImageUrl ? (
                  <img src={room.bookImageUrl} alt="" className="w-14 h-18 object-cover rounded-xl shadow-sm flex-shrink-0" />
                ) : (
                  <div className="w-14 h-18 bg-gradient-to-br from-stone-200 to-amber-100 rounded-xl flex items-center justify-center flex-shrink-0">
                    <BookOpen className="w-7 h-7 text-amber-300" />
                  </div>
                )}
                <div className="flex-1 min-w-0">
                  <h3 className="font-bold text-stone-900 truncate">{room.bookTitle}</h3>
                  <p className="text-sm text-amber-600 mt-1">
                    {formatDistanceToNow(new Date(room.updatedAt), { addSuffix: true, locale: ko })}
                  </p>
                </div>
                <button
                  onClick={(e) => handleDeleteClick(e, room.id)}
                  className="p-2 hover:bg-red-50 rounded-xl transition-all flex-shrink-0"
                >
                  <Trash2 className="w-4 h-4 text-red-400" />
                </button>
              </div>
            ))}
          </div>
        )}
      </div>

      <ConfirmModal
        isOpen={deleteTarget !== null}
        title="채팅방 삭제"
        message="이 대화를 삭제하면 되돌릴 수 없어요. 정말 삭제할까요?"
        onConfirm={handleDeleteConfirm}
        onCancel={() => setDeleteTarget(null)}
      />
    </div>
  );
}

export default ChatListPage;
