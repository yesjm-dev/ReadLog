import React, { useState, useEffect } from 'react';
import { ArrowLeft, Star, Calendar, Edit2, Trash2, Save, BookOpen, MessageCircle } from 'lucide-react';
import { useToast } from './Toast';

function BookDetailPage({ recordId, onBack, onDelete }) {
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
  const { showToast } = useToast();

  useEffect(() => {
    if (recordId) {
      fetchRecord();
    }
  }, [recordId]);

  const fetchRecord = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8080/api/reading-records/${recordId}`);
      if (!response.ok) throw new Error('불러오기 실패');
      const data = await response.json();
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
      showToast('독서 기록을 불러올 수 없습니다', "error");
      onBack();
    } finally {
      setLoading(false);
    }
  };

  const handleUpdate = async () => {
    setSaving(true);
    try {
      const response = await fetch(`http://localhost:8080/api/reading-records/${recordId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(formData)
      });

      if (!response.ok) throw new Error('수정 실패');
      
      const updated = await response.json();
      setRecord(updated);
      setIsEditing(false);
      showToast('수정되었습니다! 📝', "success");
    } catch (error) {
      showToast('수정 중 오류가 발생했습니다', "error");
      console.error(error);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!confirm('정말 삭제하시겠습니까?')) return;

    setDeleting(true);
    try {
      const response = await fetch(`http://localhost:8080/api/reading-records/${recordId}`, {
        method: 'DELETE'
      });

      if (!response.ok) throw new Error('삭제 실패');
      
      showToast('삭제되었습니다! 🗑️', "success");
      onDelete(recordId);
      onBack();
    } catch (error) {
      showToast('삭제 중 오류가 발생했습니다', "error");
      console.error(error);
    } finally {
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50 flex items-center justify-center">
        <div className="text-center">
          <div className="inline-block w-16 h-16 border-4 border-sky-400 border-t-transparent rounded-full animate-spin mb-4"></div>
          <p className="text-sky-700 font-medium">불러오는 중...</p>
        </div>
      </div>
    );
  }

  if (!record) return null;

  const statusConfig = {
    READING: { text: '읽는 중', color: 'bg-blue-400 text-white', dotColor: 'bg-blue-400' },
    COMPLETED: { text: '완독', color: 'bg-green-400 text-white', dotColor: 'bg-green-400' },
    DROPPED: { text: '중단', color: 'bg-gray-400 text-white', dotColor: 'bg-gray-400' }
  };

  const status = statusConfig[record.status] || statusConfig.COMPLETED;

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50">
      <div className="max-w-5xl mx-auto p-4 sm:p-6 lg:p-8">
        
        {/* 헤더 */}
        <div className="mb-6 sm:mb-8">
          <button
            onClick={onBack}
            className="mb-4 p-2 hover:bg-white hover:bg-opacity-70 rounded-xl transition-all inline-flex items-center gap-2 text-sky-700 font-semibold"
          >
            <ArrowLeft className="w-5 h-5" />
            책장으로 돌아가기
          </button>
        </div>

        {/* 메인 컨텐츠 */}
        <div className="bg-white rounded-3xl shadow-xl overflow-hidden">
          
          {/* 책 정보 헤더 */}
          <div className="bg-gradient-to-br from-sky-100 to-blue-100 p-6 sm:p-8">
            <div className="flex flex-col sm:flex-row gap-6">
              {/* 책 이미지 */}
              <div className="flex-shrink-0 mx-auto sm:mx-0">
                {record.bookImageUrl ? (
                  <img 
                    src={record.bookImageUrl} 
                    alt={record.bookTitle}
                    className="w-40 h-56 sm:w-48 sm:h-64 object-cover rounded-2xl shadow-lg"
                  />
                ) : (
                  <div className="w-40 h-56 sm:w-48 sm:h-64 bg-gradient-to-br from-sky-200 to-blue-200 rounded-2xl flex items-center justify-center shadow-lg">
                    <BookOpen className="w-20 h-20 text-sky-400" />
                  </div>
                )}
              </div>

              {/* 책 정보 */}
              <div className="flex-1 text-center sm:text-left">
                <h1 className="text-3xl sm:text-4xl font-bold text-sky-900 mb-3">
                  {record.bookTitle}
                </h1>
                <p className="text-xl text-sky-700 font-semibold mb-4">
                  {record.bookAuthor}
                </p>
                
                <div className="flex flex-wrap gap-3 justify-center sm:justify-start mb-4">
                  <span className={`px-4 py-2 rounded-full text-sm font-bold ${status.color} shadow-md`}>
                    {status.text}
                  </span>
                  <div className="px-4 py-2 bg-white rounded-full flex items-center gap-2 shadow-md">
                    <Star className="w-5 h-5 fill-yellow-400 text-yellow-400" />
                    <span className="font-bold text-sky-900">{record.rating}점</span>
                  </div>
                </div>

                {/* 읽은 기간 */}
                {(record.startDate || record.endDate) && (
                  <div className="flex items-center gap-2 text-sky-700 justify-center sm:justify-start">
                    <Calendar className="w-5 h-5" />
                    <span className="font-medium">
                      {record.startDate && `${record.startDate}`}
                      {record.startDate && record.endDate && ' ~ '}
                      {record.endDate && `${record.endDate}`}
                    </span>
                  </div>
                )}
              </div>
            </div>
          </div>

          {/* 내용 영역 */}
          <div className="p-6 sm:p-8">
            
            {!isEditing ? (
              // 읽기 모드
              <div className="space-y-8">
                
                {/* 나의 독서 기록 */}
                <section>
                  <h2 className="text-2xl font-bold text-sky-900 mb-4 flex items-center gap-2">
                    <MessageCircle className="w-6 h-6 text-sky-500" />
                    나의 독서 기록
                  </h2>
                  
                  <div className="bg-sky-50 rounded-2xl p-6">
                    {record.review ? (
                      <p className="text-sky-900 leading-relaxed whitespace-pre-wrap text-lg">
                        {record.review}
                      </p>
                    ) : (
                      <p className="text-sky-500 text-center py-8">
                        작성된 후기가 없습니다.
                      </p>
                    )}
                  </div>
                </section>

                {/* 나중에 추가될 섹션 */}
                <section className="border-2 border-dashed border-sky-200 rounded-2xl p-8 text-center">
                  <MessageCircle className="w-12 h-12 text-sky-300 mx-auto mb-3" />
                  <h3 className="text-lg font-bold text-sky-700 mb-2">
                    다른 사람들의 리뷰
                  </h3>
                  <p className="text-sky-500">
                    곧 다른 독자들의 리뷰를 볼 수 있어요!
                  </p>
                </section>
              </div>
            ) : (
              // 수정 모드
              <div className="space-y-6">
                <h2 className="text-2xl font-bold text-sky-900 mb-6">독서 기록 수정</h2>

                {/* 평점 */}
                <div>
                  <label className="block text-sm font-semibold text-sky-700 mb-2">평점</label>
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
                          className={`w-10 h-10 ${
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
                  <label className="block text-sm font-semibold text-sky-700 mb-2">독서 상태</label>
                  <div className="grid grid-cols-3 gap-3">
                    {['READING', 'COMPLETED', 'DROPPED'].map((s) => {
                      const config = statusConfig[s];
                      return (
                        <button
                          key={s}
                          type="button"
                          onClick={() => setFormData({ ...formData, status: s })}
                          className={`p-4 rounded-xl border-2 transition-all font-bold ${
                            formData.status === s
                              ? 'border-sky-500 bg-sky-50 text-sky-700'
                              : 'border-gray-200 hover:border-gray-300'
                          }`}
                        >
                          {config.text}
                        </button>
                      );
                    })}
                  </div>
                </div>

                {/* 날짜 */}
                <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
                  <div>
                    <label className="block text-sm font-semibold text-sky-700 mb-2">시작일</label>
                    <input
                      type="date"
                      value={formData.startDate}
                      onChange={(e) => setFormData({ ...formData, startDate: e.target.value })}
                      className="w-full px-4 py-3 border-2 border-sky-200 rounded-xl focus:border-sky-400 focus:outline-none"
                    />
                  </div>
                  <div>
                    <label className="block text-sm font-semibold text-sky-700 mb-2">종료일</label>
                    <input
                      type="date"
                      value={formData.endDate}
                      onChange={(e) => setFormData({ ...formData, endDate: e.target.value })}
                      className="w-full px-4 py-3 border-2 border-sky-200 rounded-xl focus:border-sky-400 focus:outline-none"
                    />
                  </div>
                </div>

                {/* 후기 */}
                <div>
                  <label className="block text-sm font-semibold text-sky-700 mb-2">독서 후기</label>
                  <textarea
                    value={formData.review}
                    onChange={(e) => setFormData({ ...formData, review: e.target.value })}
                    placeholder="이 책에 대한 생각을 자유롭게 적어주세요..."
                    rows="8"
                    className="w-full px-4 py-3 border-2 border-sky-200 rounded-xl focus:border-sky-400 focus:outline-none resize-none"
                  />
                </div>
              </div>
            )}
          </div>

          {/* 버튼 영역 */}
          <div className="bg-sky-50 p-6 sm:p-8 border-t border-sky-100">
            {!isEditing ? (
              <div className="flex flex-col sm:flex-row gap-3">
                <button
                  onClick={() => setIsEditing(true)}
                  className="flex-1 px-6 py-4 bg-sky-400 hover:bg-sky-500 text-white rounded-xl font-bold transition-all shadow-md flex items-center justify-center gap-2"
                >
                  <Edit2 className="w-5 h-5" />
                  수정하기
                </button>
                <button
                  onClick={handleDelete}
                  disabled={deleting}
                  className="px-6 py-4 bg-red-400 hover:bg-red-500 text-white rounded-xl font-bold transition-all shadow-md flex items-center justify-center gap-2 disabled:bg-gray-400"
                >
                  <Trash2 className="w-5 h-5" />
                  {deleting ? '삭제 중...' : '삭제하기'}
                </button>
              </div>
            ) : (
              <div className="flex flex-col sm:flex-row gap-3">
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
                  className="flex-1 px-6 py-4 border-2 border-sky-300 text-sky-700 rounded-xl font-bold hover:bg-white transition-all"
                >
                  취소
                </button>
                <button
                  onClick={handleUpdate}
                  disabled={saving}
                  className="flex-1 px-6 py-4 bg-sky-400 hover:bg-sky-500 text-white rounded-xl font-bold transition-all shadow-md flex items-center justify-center gap-2 disabled:bg-gray-400"
                >
                  <Save className="w-5 h-5" />
                  {saving ? '저장 중...' : '저장하기'}
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}

export default BookDetailPage;