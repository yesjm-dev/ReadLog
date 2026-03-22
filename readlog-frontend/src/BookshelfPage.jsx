import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { BookOpen, Star, Settings, SortAsc } from 'lucide-react';
import { api } from './api';

let cachedRecords = null;

function BookshelfPage({ onBookClick }) {
  const [records, setRecords] = useState(cachedRecords || []);
  const [loading, setLoading] = useState(!cachedRecords);
  const [error, setError] = useState('');
  const [sortBy, setSortBy] = useState('date');
  const navigate = useNavigate();

  useEffect(() => {
    if (!cachedRecords) {
      fetchRecords();
    }
  }, []);

  const fetchRecords = async () => {
    setLoading(true);
    setError('');
    try {
      const data = await api.get('/api/reading-records');
      setRecords(data);
      cachedRecords = data;
    } catch (err) {
      setError('독서 기록을 불러올 수 없습니다');
    } finally {
      setLoading(false);
    }
  };

  const sortedRecords = [...records].sort((a, b) => {
    if (sortBy === 'date') {
      const dateA = a.endDate || a.startDate || '';
      const dateB = b.endDate || b.startDate || '';
      return dateB.localeCompare(dateA);
    }
    if (sortBy === 'rating') return b.rating - a.rating;
    if (sortBy === 'title') return a.bookTitle.localeCompare(b.bookTitle);
    if (sortBy === 'author') return a.bookAuthor.localeCompare(b.bookAuthor);
    return 0;
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50">
      {/* 헤더 */}
      <div className="bg-amber-50 shadow-sm px-4 py-3 flex items-center justify-between border-b border-amber-200">
        <h1 className="font-logo text-2xl font-bold text-amber-800">ReadLog</h1>
        <button
          onClick={() => navigate('/settings')}
          className="p-2 hover:bg-amber-100 rounded-full transition-all"
        >
          <Settings className="w-5 h-5 text-stone-500" />
        </button>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-6">
        {/* 정렬 */}
        {records.length > 0 && (
          <div className="flex justify-between items-center mb-4">
            <p className="text-sm text-stone-600">{records.length}권의 소중한 책들</p>
            <div className="flex items-center gap-2 bg-white rounded-full px-3 py-1.5 shadow-sm">
              <SortAsc className="w-4 h-4 text-amber-500" />
              <select
                value={sortBy}
                onChange={(e) => setSortBy(e.target.value)}
                className="bg-transparent text-sm font-medium text-stone-900 focus:outline-none cursor-pointer"
              >
                <option value="date">최근 읽은 순</option>
                <option value="rating">평점 높은 순</option>
                <option value="title">제목 순</option>
                <option value="author">작가 순</option>
              </select>
            </div>
          </div>
        )}

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
        {!loading && !error && records.length === 0 && (
          <div className="text-center py-24">
            <BookOpen className="w-20 h-20 mx-auto text-amber-200 mb-6" />
            <p className="font-logo text-2xl text-amber-500 mb-2">책장을 채워보세요</p>
            <p className="text-amber-300 text-sm">하단의 검색 탭에서 첫 번째 책을 찾아보세요</p>
          </div>
        )}

        {/* 책 목록 */}
        {!loading && !error && sortedRecords.length > 0 && (
          <div className="grid grid-cols-3 gap-3 sm:gap-6">
            {sortedRecords.map((record, index) => (
              <BookCard
                key={record.id}
                record={record}
                index={index}
                onClick={() => onBookClick(record.id)}
              />
            ))}
          </div>
        )}
      </div>
    </div>
  );
}

function BookCard({ record, index, onClick }) {
  const statusConfig = {
    READING: { text: '읽는 중', color: 'bg-amber-500 text-white' },
    COMPLETED: { text: '완독', color: 'bg-emerald-600 text-white' },
    DROPPED: { text: '중단', color: 'bg-stone-400 text-white' }
  };

  const status = statusConfig[record.status] || statusConfig.COMPLETED;

  return (
    <div
      onClick={onClick}
      className="group cursor-pointer"
      style={{ animation: `fadeInUp 0.4s ease-out ${index * 0.05}s both` }}
    >
      <div className="bg-white rounded-xl sm:rounded-2xl shadow-lg hover:shadow-2xl transition-all duration-300 overflow-hidden hover:-translate-y-2">
        <div className="relative aspect-[3/4] bg-gradient-to-br from-stone-200 to-amber-100 overflow-hidden">
          {record.bookImageUrl ? (
            <img
              src={record.bookImageUrl}
              alt={record.bookTitle}
              className="absolute inset-0 w-full h-full object-cover"
            />
          ) : (
            <div className="absolute inset-0 flex items-center justify-center">
              <BookOpen className="w-10 h-10 sm:w-20 sm:h-20 text-amber-300" />
            </div>
          )}

          <div className="absolute top-2 left-2 sm:top-4 sm:left-4">
            <span className={`px-2 py-1 sm:px-3 sm:py-1.5 rounded-full text-xs sm:text-sm font-bold ${status.color} shadow-md`}>
              {status.text}
            </span>
          </div>

          <div className="absolute top-2 right-2 sm:top-4 sm:right-4 bg-white rounded-lg sm:rounded-xl px-2 py-1 sm:px-3 sm:py-2 shadow-md">
            <div className="flex items-center gap-0.5 sm:gap-1">
              <Star className="w-3 h-3 sm:w-5 sm:h-5 fill-yellow-400 text-yellow-400" />
              <span className="font-bold text-xs sm:text-base text-stone-900">{record.rating}</span>
            </div>
          </div>
        </div>

        <div className="p-2 sm:p-5">
          <h3 className="font-bold text-stone-900 text-xs sm:text-lg mb-1 sm:mb-2 line-clamp-2 group-hover:text-stone-600 transition-colors leading-snug min-h-[2rem] sm:min-h-[3.5rem]">
            {record.bookTitle}
          </h3>
          <p className="text-stone-600 font-medium text-xs sm:text-base truncate">{record.bookAuthor}</p>
        </div>
      </div>
    </div>
  );
}

const style = document.createElement('style');
style.textContent = `
  @keyframes fadeInUp {
    from { opacity: 0; transform: translateY(30px); }
    to { opacity: 1; transform: translateY(0); }
  }
`;
document.head.appendChild(style);

export function invalidateBookshelfCache() {
  cachedRecords = null;
}

export default BookshelfPage;
