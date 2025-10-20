import React, { useState } from 'react';
import { X, Star, Calendar, BookOpen, Check } from 'lucide-react';

function ReadingRecordModal({ book, isOpen, onClose, onSave }) {
  // 오늘 날짜를 YYYY-MM-DD 형식으로
  const today = new Date().toISOString().split('T')[0];
  
  const [formData, setFormData] = useState({
    rating: 0,
    startDate: today, // 오늘로 기본값
    endDate: '',
    review: '',
    status: 'READING' // 기본값을 읽는 중으로
  });
  const [hoverRating, setHoverRating] = useState(0);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  if (!isOpen || !book) return null;

  const handleSubmit = async () => {
    // 유효성 검사
    if (formData.rating === 0) {
      setError('평점을 선택해주세요 (읽는 중이라면 예상 평점을 입력해주세요)');
      return;
    }

    setSaving(true);
    setError('');

    try {
      const response = await fetch('http://localhost:8080/api/reading-records', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
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
        })
      });

      if (!response.ok) {
        const errorData = await response.json();
        throw new Error(errorData.message || '저장 실패');
      }

      const result = await response.json();
      onSave(result);
      
      // 폼 초기화
      setFormData({
        rating: 0,
        startDate: today, // 오늘로 리셋
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

  return (
    <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
      <div className="bg-white rounded-2xl shadow-2xl max-w-2xl w-full max-h-[90vh] overflow-y-auto">
        
        {/* 헤더 */}
        <div className="sticky top-0 bg-white border-b border-gray-200 p-4 sm:p-6 flex justify-between items-start">
          <div className="flex-1 pr-4">
            <h2 className="text-xl sm:text-2xl font-bold text-gray-800 mb-1">독서 기록 작성</h2>
            <p className="text-sm sm:text-base text-gray-600 line-clamp-1">{book.title}</p>
            <p className="text-xs sm:text-sm text-gray-500">by {book.author}</p>
          </div>
          <button
            onClick={onClose}
            className="text-gray-400 hover:text-gray-600 transition-colors"
          >
            <X className="w-6 h-6" />
          </button>
        </div>

        {/* 폼 내용 */}
        <div className="p-4 sm:p-6 space-y-6">
          
          {/* 에러 메시지 */}
          {error && (
            <div className="p-3 sm:p-4 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
              {error}
            </div>
          )}

          {/* 평점 */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              평점 <span className="text-red-500">*</span>
              <span className="text-xs text-gray-500 font-normal ml-2">
                (읽는 중이라면 예상 평점)
              </span>
            </label>
            <div className="flex gap-2">
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
                    className={`w-8 h-8 sm:w-10 sm:h-10 ${
                      star <= (hoverRating || formData.rating)
                        ? 'fill-yellow-400 text-yellow-400'
                        : 'text-gray-300'
                    }`}
                  />
                </button>
              ))}
              {formData.rating > 0 && (
                <span className="ml-2 text-lg font-semibold text-gray-700 flex items-center">
                  {formData.rating}점
                </span>
              )}
            </div>
          </div>

          {/* 상태 */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              독서 상태
            </label>
            <div className="grid grid-cols-3 gap-2">
              {[
                { value: 'READING', label: '읽는 중', icon: BookOpen, color: 'blue' },
                { value: 'COMPLETED', label: '완독', icon: Check, color: 'green' },
                { value: 'DROPPED', label: '중단', icon: X, color: 'gray' }
              ].map((status) => {
                const Icon = status.icon;
                const isSelected = formData.status === status.value;
                return (
                  <button
                    key={status.value}
                    type="button"
                    onClick={() => setFormData({ ...formData, status: status.value })}
                    className={`p-3 rounded-lg border-2 transition-all flex flex-col items-center gap-1 ${
                      isSelected
                        ? `border-${status.color}-500 bg-${status.color}-50`
                        : 'border-gray-200 hover:border-gray-300'
                    }`}
                  >
                    <Icon className={`w-5 h-5 ${isSelected ? `text-${status.color}-600` : 'text-gray-400'}`} />
                    <span className={`text-sm font-medium ${isSelected ? `text-${status.color}-700` : 'text-gray-600'}`}>
                      {status.label}
                    </span>
                  </button>
                );
              })}
            </div>
          </div>

          {/* 날짜 */}
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                <Calendar className="w-4 h-4 inline mr-1" />
                시작일
              </label>
              <input
                type="date"
                value={formData.startDate}
                onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                className="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none"
              />
            </div>
            <div>
              <label className="block text-sm font-semibold text-gray-700 mb-2">
                <Calendar className="w-4 h-4 inline mr-1" />
                종료일
              </label>
              <input
                type="date"
                value={formData.endDate}
                onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                className="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none"
              />
            </div>
          </div>

          {/* 후기 */}
          <div>
            <label className="block text-sm font-semibold text-gray-700 mb-2">
              한 줄 후기
            </label>
            <textarea
              value={formData.review}
              onChange={(e) => setFormData({ ...formData, review: e.target.value })}
              placeholder="이 책에 대한 생각을 자유롭게 적어주세요..."
              rows="4"
              className="w-full px-4 py-3 border-2 border-gray-200 rounded-lg focus:border-indigo-500 focus:outline-none resize-none"
            />
            <p className="text-xs text-gray-500 mt-1">
              {formData.review.length} / 2000자
            </p>
          </div>
        </div>

        {/* 버튼 */}
        <div className="sticky bottom-0 bg-white border-t border-gray-200 p-4 sm:p-6 flex gap-3">
          <button
            onClick={onClose}
            className="flex-1 px-6 py-3 border-2 border-gray-300 text-gray-700 rounded-xl font-semibold hover:bg-gray-50 transition-colors"
          >
            취소
          </button>
          <button
            onClick={handleSubmit}
            disabled={saving || formData.rating === 0}
            className="flex-1 px-6 py-3 bg-indigo-600 text-white rounded-xl font-semibold hover:bg-indigo-700 disabled:bg-gray-400 transition-colors"
          >
            {saving ? '저장 중...' : '저장하기'}
          </button>
        </div>
      </div>
    </div>
  );
}

export default ReadingRecordModal;