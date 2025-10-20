import React, { useState, useEffect } from 'react';
import { BookOpen, Star, Plus, SortAsc } from 'lucide-react';

function BookshelfPage({ onAddBook }) {
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [sortBy, setSortBy] = useState('date'); // date, rating, title, author

  // 데이터 불러오기
  useEffect(() => {
    fetchRecords();
  }, []);

  const fetchRecords = async () => {
    setLoading(true);
    setError('');
    
    try {
      const response = await fetch('http://localhost:8080/api/reading-records');
      if (!response.ok) throw new Error('불러오기 실패');
      const data = await response.json();
      setRecords(data);
    } catch (err) {
      setError('독서 기록을 불러올 수 없습니다');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  // 정렬
  const sortedRecords = [...records].sort((a, b) => {
    if (sortBy === 'date') {
      const dateA = a.endDate || a.startDate || '';
      const dateB = b.endDate || b.startDate || '';
      return dateB.localeCompare(dateA);
    }
    if (sortBy === 'rating') {
      return b.rating - a.rating;
    }
    if (sortBy === 'title') {
      return a.bookTitle.localeCompare(b.bookTitle);
    }
    if (sortBy === 'author') {
      return a.bookAuthor.localeCompare(b.bookAuthor);
    }
    return 0;
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50">
      <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        
        {/* 헤더 */}
        <div className="mb-8 sm:mb-12">
          <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 sm:gap-6">
            <div>
              <h1 className="text-3xl sm:text-5xl font-bold text-sky-900 mb-3 flex items-center gap-3">
                <BookOpen className="w-10 h-10 sm:w-12 sm:h-12 text-sky-500" />
                나의 책장
              </h1>
              <p className="text-base sm:text-lg text-sky-700">
                {records.length}권의 소중한 책들
              </p>
            </div>
            <button
              onClick={onAddBook}
              className="w-full sm:w-auto px-8 py-4 bg-sky-400 hover:bg-sky-500 text-white rounded-2xl font-bold text-lg transition-all shadow-lg hover:shadow-xl flex items-center justify-center gap-2"
            >
              <Plus className="w-6 h-6" />
              책 추가하기
            </button>
          </div>
        </div>

        {/* 정렬 */}
        <div className="mb-8 flex justify-end">
          <div className="inline-flex items-center gap-2 bg-white rounded-2xl px-4 py-3 shadow-md">
            <SortAsc className="w-5 h-5 text-sky-600" />
            <select
              value={sortBy}
              onChange={(e) => setSortBy(e.target.value)}
              className="bg-transparent font-semibold text-sky-900 focus:outline-none cursor-pointer"
            >
              <option value="date">최근 읽은 순</option>
              <option value="rating">평점 높은 순</option>
              <option value="title">제목 순</option>
              <option value="author">작가 순</option>
            </select>
          </div>
        </div>

        {/* 로딩 */}
        {loading && (
          <div className="text-center py-20">
            <div className="inline-block w-16 h-16 border-4 border-sky-400 border-t-transparent rounded-full animate-spin"></div>
            <p className="mt-4 text-sky-700 font-medium">불러오는 중...</p>
          </div>
        )}

        {/* 에러 */}
        {error && (
          <div className="p-6 bg-red-50 border-2 border-red-200 rounded-2xl text-red-700 mb-8">
            {error}
          </div>
        )}

        {/* 빈 상태 */}
        {!loading && !error && records.length === 0 && (
          <div className="text-center py-20 bg-white rounded-3xl shadow-lg">
            <BookOpen className="w-24 h-24 mx-auto text-sky-200 mb-6" />
            <h3 className="text-2xl font-bold text-sky-900 mb-3">
              아직 읽은 책이 없어요
            </h3>
            <p className="text-sky-600 mb-8 text-lg">
              첫 번째 책을 추가해보세요!
            </p>
            <button
              onClick={onAddBook}
              className="px-8 py-4 bg-sky-400 hover:bg-sky-500 text-white rounded-2xl font-bold text-lg transition-all shadow-lg inline-flex items-center gap-2"
            >
              <Plus className="w-6 h-6" />
              책 추가하기
            </button>
          </div>
        )}

        {/* 책 목록 */}
        {!loading && !error && sortedRecords.length > 0 && (
          <div className="grid grid-cols-3 gap-3 sm:gap-6 lg:gap-8">
            {sortedRecords.map((record, index) => (
              <BookCard key={record.id} record={record} index={index} />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

// 책 카드 컴포넌트
function BookCard({ record, index }) {
  const statusConfig = {
    READING: { text: '읽는 중', color: 'bg-blue-400 text-white' },
    COMPLETED: { text: '완독', color: 'bg-green-400 text-white' },
    DROPPED: { text: '중단', color: 'bg-gray-400 text-white' }
  };
  
  const status = statusConfig[record.status] || statusConfig.COMPLETED;

  return (
    <div 
      className="group cursor-pointer"
      style={{
        animation: `fadeInUp 0.5s ease-out ${index * 0.1}s both`
      }}
    >
      <div className="bg-white rounded-xl sm:rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden hover:-translate-y-2">
        {/* 책 이미지 */}
        <div className="relative aspect-[3/4] bg-gradient-to-br from-sky-100 to-indigo-100 overflow-hidden">
          {/* 실제 이미지가 있으면 표시, 없으면 placeholder */}
          {record.bookImageUrl ? (
            <img 
              src={record.bookImageUrl} 
              alt={record.bookTitle}
              className="absolute inset-0 w-full h-full object-cover"
            />
          ) : (
            <div className="absolute inset-0 flex items-center justify-center">
              <BookOpen className="w-10 h-10 sm:w-20 sm:h-20 text-sky-300" />
            </div>
          )}
          
          {/* 상태 뱃지 */}
          <div className="absolute top-2 left-2 sm:top-4 sm:left-4">
            <span className={`px-2 py-1 sm:px-3 sm:py-1.5 rounded-full text-xs sm:text-sm font-bold ${status.color} shadow-md`}>
              {status.text}
            </span>
          </div>

          {/* 평점 */}
          <div className="absolute top-2 right-2 sm:top-4 sm:right-4 bg-white rounded-lg sm:rounded-xl px-2 py-1 sm:px-3 sm:py-2 shadow-md">
            <div className="flex items-center gap-0.5 sm:gap-1">
              <Star className="w-3 h-3 sm:w-5 sm:h-5 fill-yellow-400 text-yellow-400" />
              <span className="font-bold text-xs sm:text-base text-sky-900">{record.rating}</span>
            </div>
          </div>
        </div>

        {/* 책 정보 */}
        <div className="p-2 sm:p-5">
          <h3 className="font-bold text-sky-900 text-xs sm:text-lg mb-1 sm:mb-2 line-clamp-2 group-hover:text-sky-600 transition-colors leading-snug min-h-[2rem] sm:min-h-[3.5rem]">
            {record.bookTitle}
          </h3>
          <p className="text-sky-600 font-medium text-xs sm:text-base truncate">{record.bookAuthor}</p>
        </div>
      </div>
    </div>
  );
}

// CSS 애니메이션 추가
const style = document.createElement('style');
style.textContent = `
  @keyframes fadeInUp {
    from {
      opacity: 0;
      transform: translateY(30px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
`;
document.head.appendChild(style);

export default BookshelfPage;