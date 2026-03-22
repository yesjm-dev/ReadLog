import React, { useState } from 'react';
import { X, Star, BookOpen, Check } from 'lucide-react';
import { api } from './api';

function ReadingRecordModal({ book, isOpen, onClose, onSave }) {
  const today = new Date().toISOString().split('T')[0];

  const [formData, setFormData] = useState({
    rating: 0,
    startDate: today,
    endDate: '',
    review: '',
    status: 'READING'
  });
  const [hoverRating, setHoverRating] = useState(0);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen || !book) return null;

  const isCompleted = formData.status === 'COMPLETED';

  const handleSubmit = async () => {
    if (isCompleted && formData.rating === 0) {
      setError('완독한 책은 평점을 남겨주세요.');
      return;
    }

    setSaving(true);
    setError('');

    try {
      const result = await api.post('/api/reading-records', {
        book: {
          id: book.id || null,
          title: book.title,
          author: book.author,
          isbn: book.isbn,
          imageUrl: book.imageUrl,
          publisher: book.publisher,
          description: book.description
        },
        rating: formData.rating,
        startDate: formData.startDate || null,
        endDate: formData.endDate || null,
        review: formData.review || null,
        status: formData.status
      });

      onSave(result);

      const newToday = new Date().toISOString().split('T')[0];
      setFormData({
        rating: 0,
        startDate: newToday,
        endDate: '',
        review: '',
        status: 'READING'
      });

      onClose();
    } catch (err) {
      setError(err.message || '저장 중 오류가 발생했습니다');
    } finally {
      setSaving(false);
    }
  };

  const statusOptions = [
    { value: 'READING', label: '읽는 중', activeClass: 'border-amber-400 bg-amber-50 text-amber-800' },
    { value: 'COMPLETED', label: '완독', activeClass: 'border-emerald-400 bg-emerald-50 text-emerald-800' },
    { value: 'DROPPED', label: '중단', activeClass: 'border-stone-400 bg-stone-50 text-stone-700' }
  ];

  return (
    <div className="fixed inset-0 bg-black/40 flex items-end sm:items-center justify-center z-50">
      <div className="bg-white rounded-t-2xl sm:rounded-2xl shadow-2xl w-full sm:max-w-lg max-h-[85vh] overflow-y-auto">

        {/* 헤더 */}
        <div className="sticky top-0 bg-white border-b border-stone-100 px-5 py-4 flex justify-between items-center">
          <div className="flex-1 min-w-0 pr-4">
            <h2 className="font-logo text-lg font-bold text-amber-800">독서 기록</h2>
            <p className="text-sm text-stone-500 truncate">{book.title}</p>
          </div>
          <button onClick={onClose} className="p-1.5 hover:bg-stone-100 rounded-full transition-all">
            <X className="w-5 h-5 text-stone-400" />
          </button>
        </div>

        {/* 폼 */}
        <div className="px-5 py-5 space-y-5">

          {error && (
            <div className="p-3 bg-red-50 border border-red-200 rounded-lg text-red-600 text-sm">
              {error}
            </div>
          )}

          {/* 독서 상태 */}
          <div>
            <label className="block text-sm font-medium text-stone-600 mb-2">독서 상태</label>
            <div className="grid grid-cols-3 gap-2">
              {statusOptions.map((status) => (
                <button
                  key={status.value}
                  type="button"
                  onClick={() => setFormData({ ...formData, status: status.value })}
                  className={`py-2.5 rounded-lg border-2 transition-all font-medium text-sm ${
                    formData.status === status.value
                      ? status.activeClass
                      : 'border-stone-200 text-stone-400 hover:border-stone-300'
                  }`}
                >
                  {status.label}
                </button>
              ))}
            </div>
          </div>

          {/* 평점 */}
          <div>
            <label className="block text-sm font-medium text-stone-600 mb-2">
              평점{isCompleted && <span className="text-red-400 ml-1">*</span>}
            </label>
            <div className="flex items-center gap-1">
              {[1, 2, 3, 4, 5].map((star) => (
                <button
                  key={star}
                  type="button"
                  onClick={() => setFormData({ ...formData, rating: formData.rating === star ? 0 : star })}
                  onMouseEnter={() => setHoverRating(star)}
                  onMouseLeave={() => setHoverRating(0)}
                  className="transition-transform hover:scale-110"
                >
                  <Star
                    className={`w-8 h-8 ${
                      star <= (hoverRating || formData.rating)
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-stone-200'
                    }`}
                  />
                </button>
              ))}
              {formData.rating > 0 && (
                <span className="ml-2 text-sm font-medium text-stone-500">{formData.rating}점</span>
              )}
            </div>
            {!isCompleted && formData.rating === 0 && (
              <p className="text-xs text-stone-400 mt-1">나중에 입력해도 괜찮아요</p>
            )}
          </div>

          {/* 날짜 */}
          <div className="grid grid-cols-2 gap-3">
            <div>
              <label className="block text-sm font-medium text-stone-600 mb-2">시작일</label>
              <input
                type="date"
                value={formData.startDate}
                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-500 focus:outline-none text-sm"
              />
            </div>
            <div>
              <label className="block text-sm font-medium text-stone-600 mb-2">종료일</label>
              <input
                type="date"
                value={formData.endDate}
                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-500 focus:outline-none text-sm"
              />
            </div>
          </div>

          {/* 후기 */}
          <div>
            <label className="block text-sm font-medium text-stone-600 mb-2">한 줄 기록</label>
            <textarea
              value={formData.review}
              onChange={(e) => setFormData({ ...formData, review: e.target.value })}
              placeholder="이 책에 대한 생각을 자유롭게..."
              rows="3"
              className="w-full px-3 py-2.5 border-2 border-stone-200 rounded-lg focus:border-amber-500 focus:outline-none resize-none text-sm"
            />
          </div>
        </div>

        {/* 버튼 */}
        <div className="sticky bottom-0 bg-white border-t border-stone-100 px-5 py-4 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 py-3 bg-stone-100 text-stone-600 rounded-lg font-medium hover:bg-stone-200 transition-all text-sm"
          >
            취소
          </button>
          <button
            onClick={handleSubmit}
            disabled={saving || (isCompleted && formData.rating === 0)}
            className="flex-1 py-3 bg-amber-100 text-amber-800 rounded-lg font-medium transition-all hover:bg-amber-200 text-sm disabled:bg-stone-100 disabled:text-stone-400"
          >
            {saving ? '저장 중...' : '기록하기'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ReadingRecordModal;
