import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Search, Book, BookOpen, Star, MessageCircle } from 'lucide-react';
import ReadingRecordModal from './ReadingRecordModal';
import { useToast } from './Toast';
import { searchBooks, api } from './api';
import { invalidateBookshelfCache } from './BookshelfPage';

// 검색 상태를 모듈 레벨에 보관 (탭 전환 시 유지, 새로고침 시 초기화)
let cachedQuery = '';
let cachedBooks = [];

function BookSearchApp({ onGoToNewChat }) {
  const navigate = useNavigate();
  const [query, setQuery] = useState(cachedQuery);
  const [books, setBooks] = useState(cachedBooks);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedBook, setSelectedBook] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { showToast } = useToast();

  const handleSearch = async (directQuery) => {
  const searchQuery = (directQuery || query).trim();
  if (!searchQuery) {
    setError('검색어를 입력해주세요');
    return;
  }

  setQuery(searchQuery);
  cachedQuery = searchQuery;
  setLoading(true);
  setError('');
  setBooks([]);

  try {
    const results = await searchBooks(searchQuery);
    setBooks(results);
    cachedBooks = results;
    if (results.length === 0) {
      setError('검색 결과가 없습니다');
    }
  } catch (err) {
    setError('검색 중 오류가 발생했습니다');
  } finally {
    setLoading(false);
  }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      handleSearch();
    }
  };

  const handleSelectBook = (book) => {
    setSelectedBook(book);
    setIsModalOpen(true);
  };

  const handleAskAi = (book) => {
    if (onGoToNewChat) {
      onGoToNewChat(book);
    }
  };

  const handleSaveRecord = (savedRecord) => {
    console.log('저장 완료:', savedRecord);

    invalidateBookshelfCache();
    showToast(`"${savedRecord.bookTitle}" 독서 기록이 저장되었습니다!`, "success");
    navigate('/');
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-stone-100 via-amber-50 to-orange-50">
      <div className="max-w-5xl mx-auto p-4 sm:p-6 lg:p-8">

        {/* 헤더 */}
        <div className="mb-6">
          <h1 className="font-logo text-2xl font-bold text-amber-600 mb-1">어떤 책을 만났나요?</h1>
          <p className="text-stone-500 text-sm">읽고 싶은 책을 찾아보세요</p>
        </div>

        {/* 검색 영역 */}
        <div className="mb-6 sm:mb-8">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-amber-500 w-5 h-5" />
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="책 제목이나 저자 검색..."
                className="w-full pl-12 pr-4 py-4 rounded-2xl border-2 border-amber-200 focus:border-amber-600 focus:outline-none text-lg bg-white shadow-md"
              />
            </div>
            <button
              onClick={handleSearch}
              disabled={loading}
              className="w-full sm:w-auto px-8 py-4 bg-amber-600 hover:bg-amber-700 text-white rounded-2xl font-bold transition-all shadow-md disabled:bg-gray-300"
            >
              {loading ? '검색 중...' : '검색'}
            </button>
          </div>
        </div>

        {/* 추천 키워드 */}
        {books.length === 0 && !loading && !error && (
          <div className="mb-8">
            <p className="text-stone-500 text-sm mb-3">이런 책은 어떠세요?</p>
            <div className="flex flex-wrap gap-2">
              {['에세이', '소설', '자기계발', '심리학', '경제', '과학', '여행', '시'].map((keyword) => (
                <button
                  key={keyword}
                  onClick={() => handleSearch(keyword)}
                  className="px-4 py-2 bg-white border border-amber-200 text-stone-700 rounded-full text-sm hover:bg-amber-50 hover:border-amber-400 transition-all shadow-sm"
                >
                  {keyword}
                </button>
              ))}
            </div>
          </div>
        )}

        {/* 에러 메시지 */}
        {error && (
          <div className="mb-6 p-4 bg-red-50 border-2 border-red-200 rounded-2xl text-red-700">
            {error}
          </div>
        )}

        {/* 검색 결과 */}
        {books.length > 0 && (
          <div>
            <h2 className="text-xl sm:text-2xl font-bold text-stone-900 mb-4 sm:mb-6">
              검색 결과 <span className="text-amber-600">{books.length}권</span>
            </h2>
            <div className="space-y-4">
              {books.map((book, index) => (
                <BookListItem
                  key={index}
                  book={book}
                  onSelect={() => handleSelectBook(book)}
                  onAskAi={() => handleAskAi(book)}
                />
              ))}
            </div>
          </div>
        )}

        {/* 선택한 책 미리보기 (제거) */}

        {/* 독서 기록 모달 */}
        <ReadingRecordModal
          book={selectedBook}
          isOpen={isModalOpen}
          onClose={() => setIsModalOpen(false)}
          onSave={handleSaveRecord}
        />
      </div>
    </div>
  );
}

// 책 리스트 아이템 컴포넌트
function BookListItem({ book, onSelect, onAskAi }) {
  // 설명 글자수 제한
  const truncateText = (text, maxLength) => {
    if (!text) return '';
    if (text.length <= maxLength) return text;
    return text.substring(0, maxLength) + '...';
  };

  return (
    <div
      onClick={onSelect}
      className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all cursor-pointer overflow-hidden group border-2 border-stone-200 hover:border-amber-300"
    >
      <div className="flex gap-4 p-4 sm:p-5">
        {/* 책 이미지 */}
        <div className="flex-shrink-0">
          {book.imageUrl ? (
            <img
              src={book.imageUrl}
              alt={book.title}
              className="w-20 h-28 sm:w-24 sm:h-32 object-cover rounded-xl shadow-md"
            />
          ) : (
            <div className="w-20 h-28 sm:w-24 sm:h-32 bg-gradient-to-br from-stone-200 to-amber-100 rounded-xl flex items-center justify-center shadow-md">
              <Book className="w-10 h-10 text-amber-300" />
            </div>
          )}
        </div>

        {/* 책 정보 + 버튼 */}
        <div className="flex-1 min-w-0 flex flex-col justify-between">
          <div>
            <h3 className="font-bold text-stone-900 text-base sm:text-lg mb-1 line-clamp-1 group-hover:text-stone-600 transition-colors">
              {book.title}
            </h3>
            <p className="text-stone-500 text-sm truncate">{book.author}{book.publisher ? ` · ${book.publisher}` : ''}</p>
            {book.description && (
              <p className="text-xs text-stone-400 leading-relaxed mt-1 line-clamp-1">
                {book.description}
              </p>
            )}
          </div>

          {/* 버튼 영역 */}
          <div className="flex gap-2 mt-3">
            <button
              className="flex-1 py-2.5 bg-amber-100 text-amber-800 rounded-lg font-medium transition-all hover:bg-amber-200 flex items-center justify-center gap-1.5 text-xs sm:text-sm"
            >
              <BookOpen className="w-3.5 h-3.5" />
              기록하기
            </button>
            <button
              onClick={(e) => { e.stopPropagation(); onAskAi(); }}
              className="flex-1 py-2.5 bg-stone-100 text-stone-700 rounded-lg font-medium transition-all hover:bg-stone-200 flex items-center justify-center gap-1.5 text-xs sm:text-sm"
            >
              <MessageCircle className="w-3.5 h-3.5" />
              AI 추천
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default BookSearchApp;
