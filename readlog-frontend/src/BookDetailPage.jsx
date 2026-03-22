import React, { useState, useEffect } from 'react';
import { ArrowLeft, Star, Calendar, Edit2, Trash2, Save, BookOpen, MessageCircle, Check } from 'lucide-react';
import { useToast } from './Toast';
import { api } from './api';
import ConfirmModal from './ConfirmModal';
import { invalidateBookshelfCache } from './BookshelfPage';

function BookDetailPage({ recordId, onBack, onDelete, onGoToChat, onGoToNewChat }) {
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    rating: 0,
    startDate: '',
    endDate: '',
    review: '',
    status: 'READING'
  });
  const [hoverRating, setHoverRating] = useState(0);
  const [saving, setSaving] = useState(false);
  const [deleting, setDeleting] = useState(false);
  const [showDeleteConfirm, setShowDeleteConfirm] = useState(false);
  const { showToast } = useToast();

  useEffect(() => {
    if (recordId) {
      fetchRecord();
    }
  }, [recordId]);

  const fetchRecord = async () => {
    setLoading(true);
    try {
      const data = await api.get(`/api/reading-records/${recordId}`);
      setRecord(data);
      setFormData({
        rating: data.rating,
        startDate: data.startDate || '',
        endDate: data.endDate || '',
        review: data.review || '',
        status: data.status
      });
    } catch (error) {
      console.error(error);
      showToast('독서 기록을 불러올 수 없습니다', 'error');
      onBack();
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    setSaving(true);
    try {
      const updated = await api.put(`/api/reading-records/${recordId}`, formData);
      setRecord(updated);
      setIsEditing(false);
      invalidateBookshelfCache();
      showToast('수정되었습니다!', 'success');
    } catch (error) {
      showToast('수정 중 오류가 발생했습니다', 'error');
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    setDeleting(true);
    try {
      await api.delete(`/api/reading-records/${recordId}`);
      invalidateBookshelfCache();
      showToast('삭제되었습니다!', 'success');
      onDelete(recordId);
      onBack();
    } catch (error) {
      showToast('삭제 중 오류가 발생했습니다', 'error');
    } finally {
      setDeleting(false);
      setShowDeleteConfirm(false);
    }
  };

  const getReadingDays = () => {
    if (!record?.startDate) return null;
    const start = new Date(record.startDate);
    const end = record.endDate ? new Date(record.endDate) : new Date();
    const days = Math.ceil((end - start) / (1000 * 60 * 60 * 24));
    return days > 0 ? days : 1;
  };

  const handleAskAi = async () => {
    try {
      const chatRoom = await api.post('/api/chat-rooms', { bookId: record.bookId });
      if (onGoToChat) onGoToChat(chatRoom.id);
    } catch (err) {
      // 채팅방이 없으면 새로 생성
      if (onGoToNewChat) {
        onGoToNewChat({
          title: record.bookTitle,
          author: record.bookAuthor,
          imageUrl: record.bookImageUrl,
        });
      }
    }
  };

  const handleComplete = async () => {
    try {
      const today = new Date().toISOString().split('T')[0];
      const updated = await api.put(`/api/reading-records/${recordId}`, {
        ...formData,
        status: 'COMPLETED',
        endDate: today,
        rating: record.rating || 0,
      });
      setRecord(updated);
      setFormData({
        rating: updated.rating,
        startDate: updated.startDate || '',
        endDate: updated.endDate || '',
        review: updated.review || '',
        status: updated.status
      });
      invalidateBookshelfCache();
      showToast('완독을 축하해요!', 'success');
    } catch (error) {
      showToast('완독 처리 중 오류가 발생했습니다', 'error');
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

  if (!record) return null;

  const statusConfig = {
    READING: { text: '읽는 중', color: 'bg-amber-500 text-white' },
    COMPLETED: { text: '완독', color: 'bg-emerald-600 text-white' },
    DROPPED: { text: '중단', color: 'bg-stone-400 text-white' }
  };

  const status = statusConfig[record.status] || statusConfig.COMPLETED;

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50">
      {/* 헤더 */}
      <div className="bg-amber-50 shadow-sm px-4 py-3 flex items-center gap-3 border-b border-amber-200">
        <button onClick={onBack} className="p-2 hover:bg-amber-100 rounded-full transition-all">
          <ArrowLeft className="w-5 h-5 text-amber-800" />
        </button>
        <h1 className="text-lg font-bold text-stone-800 truncate flex-1">{record.bookTitle}</h1>
      </div>

      <div className="max-w-2xl mx-auto p-4 sm:p-6">
        {/* 책 정보 카드 */}
        <div className="bg-white rounded-2xl shadow-md overflow-hidden">
          <div className="bg-gradient-to-br from-stone-200 to-amber-100 p-5 sm:p-6">
            <div className="flex gap-5">
              <div className="flex-shrink-0">
                {record.bookImageUrl ? (
                  <img
                    src={record.bookImageUrl}
                    alt={record.bookTitle}
                    className="w-28 h-40 sm:w-32 sm:h-44 object-cover rounded-xl shadow-lg"
                  />
                ) : (
                  <div className="w-28 h-40 sm:w-32 sm:h-44 bg-gradient-to-br from-stone-300 to-amber-200 rounded-xl flex items-center justify-center shadow-lg">
                    <BookOpen className="w-14 h-14 text-amber-400" />
                  </div>
                )}
              </div>

              <div className="flex-1 min-w-0 flex flex-col justify-between">
                <div>
                  <h2 className="text-xl sm:text-2xl font-bold text-stone-900 mb-1 line-clamp-2">
                    {record.bookTitle}
                  </h2>
                  <p className="text-stone-600 font-medium mb-3">{record.bookAuthor}</p>
                </div>

                <div className="flex flex-wrap gap-2 items-center">
                  <span className={`px-3 py-1 rounded-full text-xs font-bold ${status.color}`}>
                    {status.text}
                  </span>
                  {record.rating > 0 && (
                    <div className="px-3 py-1 bg-white rounded-full flex items-center gap-1 shadow-sm">
                      <Star className="w-3.5 h-3.5 fill-yellow-400 text-yellow-400" />
                      <span className="font-bold text-sm text-stone-900">{record.rating}</span>
                    </div>
                  )}
                  {record.status === 'READING' && (
                    <button
                      onClick={handleComplete}
                      className="px-3 py-1 bg-white rounded-full text-xs font-medium text-emerald-700 shadow-sm hover:bg-emerald-50 transition-all flex items-center gap-1"
                    >
                      <Check className="w-3 h-3" />
                      다 읽었어요
                    </button>
                  )}
                </div>

                {(record.startDate || record.endDate) && (
                  <div className="flex items-center gap-1.5 text-stone-500 text-xs mt-2">
                    <Calendar className="w-3.5 h-3.5" />
                    <span>
                      {record.startDate}{record.startDate && record.endDate && ' ~ '}{record.endDate}
                    </span>
                    {getReadingDays() && (
                      <span className="text-stone-400">
                        · {record.status === 'READING'
                          ? `${getReadingDays()}일째 읽고 있어요`
                          : `${getReadingDays()}일간 읽었어요`
                        }
                      </span>
                    )}
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 내용 영역 */}
          <div className="p-5 sm:p-6">
            {!isEditing ? (
              <div>
                {/* 나의 기록 */}
                <div className="bg-amber-50/60 rounded-xl p-6 mb-5 border border-amber-100">
                  <div className="flex items-center gap-2 mb-2">
                    <Edit2 className="w-3.5 h-3.5 text-amber-600" />
                    <span className="text-xs font-medium text-amber-700">나의 기록</span>
                  </div>
                  {record.review ? (
                    <p className="font-review text-stone-700 leading-loose whitespace-pre-wrap text-sm">
                      {record.review}
                    </p>
                  ) : (
                    <p className="text-stone-400 text-sm italic">
                      아직 기록이 없어요. 수정하기를 눌러 작성해보세요.
                    </p>
                  )}
                </div>

                {/* 구분선 + 책 소개 */}
                {record.bookDescription && (
                  <div className="border-t border-stone-100 pt-4 mb-5">
                    <p className="text-xs text-stone-400 mb-2">책 소개</p>
                    <p className="text-stone-400 leading-relaxed text-xs">
                      {record.bookDescription}
                    </p>
                  </div>
                )}

                {/* AI 대화 버튼 */}
                <button
                  onClick={handleAskAi}
                  className="w-full py-2.5 border border-stone-200 text-stone-500 rounded-lg text-xs font-medium transition-all hover:bg-stone-50 flex items-center justify-center gap-1.5"
                >
                  <MessageCircle className="w-3.5 h-3.5" />
                  이 책에 대해 AI와 이야기하기
                </button>
              </div>
            ) : (
              <div className="space-y-5">
                <h3 className="font-logo text-lg font-bold text-amber-800">기록 수정</h3>

                {/* 평점 */}
                <div>
                  <label className="block text-sm font-medium text-stone-600 mb-2">평점</label>
                  <div className="flex gap-1.5">
                    {[1, 2, 3, 4, 5].map((star) => (
                      <button
                        key={star}
                        type="button"
                        onClick={() => setFormData({ ...formData, rating: star })}
                        onMouseEnter={() => setHoverRating(star)}
                        onMouseLeave={() => setHoverRating(0)}
                        className="transition-transform hover:scale-110"
                      >
                        <Star
                          className={`w-8 h-8 ${
                            star <= (hoverRating || formData.rating)
                              ? 'fill-yellow-400 text-yellow-400'
                              : 'text-gray-300'
                          }`}
                        />
                      </button>
                    ))}
                  </div>
                </div>

                {/* 상태 */}
                <div>
                  <label className="block text-sm font-medium text-stone-600 mb-2">독서 상태</label>
                  <div className="grid grid-cols-3 gap-2">
                    {['READING', 'COMPLETED', 'DROPPED'].map((s) => {
                      const config = statusConfig[s];
                      return (
                        <button
                          key={s}
                          type="button"
                          onClick={() => setFormData({ ...formData, status: s })}
                          className={`py-3 rounded-lg border-2 transition-all font-medium text-sm ${
                            formData.status === s
                              ? 'border-amber-600 bg-amber-50 text-amber-800'
                              : 'border-stone-200 text-stone-500 hover:border-stone-300'
                          }`}
                        >
                          {config.text}
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* 날짜 */}
                <div className="grid grid-cols-2 gap-3">
                  <div>
                    <label className="block text-sm font-medium text-stone-600 mb-2">시작일</label>
                    <input
                      type="date"
                      value={formData.startDate}
                      onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                      className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-600 focus:outline-none text-sm"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-medium text-stone-600 mb-2">종료일</label>
                    <input
                      type="date"
                      value={formData.endDate}
                      onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                      className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-600 focus:outline-none text-sm"
                    />
                  </div>
                </div>

                {/* 후기 */}
                <div>
                  <label className="block text-sm font-medium text-stone-600 mb-2">독서 후기</label>
                  <textarea
                    value={formData.review}
                    onChange={(e) => setFormData({ ...formData, review: e.target.value })}
                    placeholder="이 책에 대한 생각을 자유롭게 적어주세요..."
                    rows="6"
                    className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-600 focus:outline-none resize-none text-sm"
                  />
                </div>
              </div>
            )}
          </div>

          {/* 버튼 영역 */}
          <div className="p-5 sm:p-6 border-t border-stone-100">
            {!isEditing ? (
              <div className="space-y-2">
                <div className="flex gap-3">
                  <button
                    onClick={() => setIsEditing(true)}
                    className="flex-1 py-3 bg-amber-100 text-amber-800 rounded-lg font-medium transition-all hover:bg-amber-200 flex items-center justify-center gap-2 text-sm"
                  >
                    <Edit2 className="w-4 h-4" />
                    수정하기
                  </button>
                  <button
                    onClick={() => setShowDeleteConfirm(true)}
                    disabled={deleting}
                    className="py-3 px-5 bg-stone-100 text-stone-500 rounded-lg font-medium transition-all hover:bg-red-50 hover:text-red-500 flex items-center justify-center gap-2 text-sm"
                  >
                    <Trash2 className="w-4 h-4" />
                    삭제
                  </button>
                </div>
              </div>
            ) : (
              <div className="flex gap-3">
                <button
                  onClick={() => {
                    setIsEditing(false);
                    setFormData({
                      rating: record.rating,
                      startDate: record.startDate || '',
                      endDate: record.endDate || '',
                      review: record.review || '',
                      status: record.status
                    });
                  }}
                  className="flex-1 py-3 bg-stone-100 text-stone-600 rounded-lg font-medium hover:bg-stone-200 transition-all text-sm"
                >
                  취소
                </button>
                <button
                  onClick={handleUpdate}
                  disabled={saving}
                  className="flex-1 py-3 bg-amber-100 text-amber-800 rounded-lg font-medium transition-all hover:bg-amber-200 flex items-center justify-center gap-2 text-sm disabled:bg-gray-200 disabled:text-gray-400"
                >
                  <Save className="w-4 h-4" />
                  {saving ? '저장 중...' : '저장하기'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>

      <ConfirmModal
        isOpen={showDeleteConfirm}
        title="독서 기록 삭제"
        message="이 기록을 삭제하면 되돌릴 수 없어요. 정말 삭제할까요?"
        onConfirm={handleDelete}
        onCancel={() => setShowDeleteConfirm(false)}
      />
    </div>
  );
}

export default BookDetailPage;
