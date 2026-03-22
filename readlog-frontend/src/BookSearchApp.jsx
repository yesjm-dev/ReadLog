import React, { useState } from 'react';
import { Search, Book, Star, ArrowLeft, MessageCircle } from 'lucide-react';
import ReadingRecordModal from './ReadingRecordModal';
import { useToast } from './Toast';
import { searchBooks, api } from './api';

function BookSearchApp({ onGoToBookshelf, onGoToChat }) {
  const [query, setQuery] = useState('');
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [selectedBook, setSelectedBook] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { showToast } = useToast();

  const handleSearch = async () => {
  if (!query.trim()) {
    setError('검색어를 입력해주세요');
    return;
  }

  setLoading(true);
  setError('');
  setBooks([]);

  try {
    const results = await searchBooks(query);
    setBooks(results);
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

  const handleAskAi = async (book) => {
    try {
      // 먼저 책을 저장 (DB에 없을 수 있으므로)
      const savedBook = await api.post('/api/books', {
        title: book.title,
        author: book.author,
        isbn: book.isbn,
        imageUrl: book.imageUrl,
        publisher: book.publisher,
        description: book.description
      });
      // 채팅방 생성 또는 기존 반환
      const chatRoom = await api.post('/api/chat-rooms', { bookId: savedBook.id });
      if (onGoToChat) {
        onGoToChat(chatRoom.id);
      }
    } catch (err) {
      showToast('AI 채팅방 생성에 실패했습니다', 'error');
    }
  };

  const handleSaveRecord = (savedRecord) => {
    console.log('저장 완료:', savedRecord);
    
    if (onGoToBookshelf) {
      showToast(`"${savedRecord.bookTitle}" 독서 기록이 저장되었습니다! 🎉\n책장으로 이동합니다.`, "success");
      onGoToBookshelf();
    } else {
      showToast(`"${savedRecord.bookTitle}" 독서 기록이 저장되었습니다! 🎉`, "success");
    }
  };

  return (
    <div className="min-h-screen bg-gradient-to-br from-sky-50 via-blue-50 to-indigo-50">
      <div className="max-w-5xl mx-auto p-4 sm:p-6 lg:p-8">
        
        {/* 헤더 */}
        <div className="mb-6 sm:mb-8">
          <div className="flex items-center gap-3 mb-3">
            {onGoToBookshelf && (
              <button
                onClick={onGoToBookshelf}
                className="p-2 hover:bg-white hover:bg-opacity-70 rounded-xl transition-all"
              >
                <ArrowLeft className="w-6 h-6 text-sky-700" />
              </button>
            )}
            <h1 className="text-3xl sm:text-5xl font-bold text-sky-900 flex items-center gap-3">
              <Search className="w-8 h-8 sm:w-10 sm:h-10 text-sky-500" />
              책 검색
            </h1>
          </div>
          <p className="text-base sm:text-lg text-sky-700 ml-0 sm:ml-14">읽고 싶은 책을 찾아보세요</p>
        </div>

        {/* 검색 영역 */}
        <div className="mb-6 sm:mb-8">
          <div className="flex flex-col sm:flex-row gap-3">
            <div className="flex-1 relative">
              <Search className="absolute left-4 top-1/2 transform -translate-y-1/2 text-sky-400 w-5 h-5" />
              <input
                type="text"
                value={query}
                onChange={(e) => setQuery(e.target.value)}
                onKeyPress={handleKeyPress}
                placeholder="책 제목이나 저자 검색..."
                className="w-full pl-12 pr-4 py-4 rounded-2xl border-2 border-sky-200 focus:border-sky-400 focus:outline-none text-lg bg-white shadow-md"
              />
            </div>
            <button
              onClick={handleSearch}
              disabled={loading}
              className="w-full sm:w-auto px-8 py-4 bg-sky-400 hover:bg-sky-500 text-white rounded-2xl font-bold transition-all shadow-md disabled:bg-gray-300"
            >
              {loading ? '검색 중...' : '검색'}
            </button>
          </div>
        </div>

        {/* 에러 메시지 */}
        {error && (
          <div className="mb-6 p-4 bg-red-50 border-2 border-red-200 rounded-2xl text-red-700">
            {error}
          </div>
        )}

        {/* 검색 결과 */}
        {books.length > 0 && (
          <div>
            <h2 className="text-xl sm:text-2xl font-bold text-sky-900 mb-4 sm:mb-6">
              검색 결과 <span className="text-sky-500">{books.length}권</span>
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
      className="bg-white rounded-2xl shadow-md hover:shadow-xl transition-all cursor-pointer overflow-hidden group border-2 border-sky-100 hover:border-sky-300"
    >
      <div className="flex flex-col sm:flex-row gap-4 p-4 sm:p-5">
        {/* 책 이미지 */}
        <div className="flex-shrink-0 mx-auto sm:mx-0">
          {book.imageUrl ? (
            <img 
              src={book.imageUrl} 
              alt={book.title}
              className="w-24 h-32 sm:w-28 sm:h-36 object-cover rounded-xl shadow-md"
            />
          ) : (
            <div className="w-24 h-32 sm:w-28 sm:h-36 bg-gradient-to-br from-sky-100 to-blue-100 rounded-xl flex items-center justify-center shadow-md">
              <Book className="w-12 h-12 text-sky-300" />
            </div>
          )}
        </div>

        {/* 책 정보 */}
        <div className="flex-1 min-w-0 text-center sm:text-left">
          <h3 className="font-bold text-sky-900 text-lg sm:text-xl mb-2 line-clamp-2 group-hover:text-sky-600 transition-colors">
            {book.title}
          </h3>
          <p className="text-sky-600 font-semibold mb-2 text-base">{book.author}</p>
          
          {book.publisher && (
            <p className="text-sm text-sky-500 mb-3">
              {book.publisher}
            </p>
          )}
          
          {book.description && (
            <p className="text-sm text-sky-700 leading-relaxed">
              <span className="sm:hidden">{truncateText(book.description, 60)}</span>
              <span className="hidden sm:inline">{truncateText(book.description, 60)}</span>
            </p>
          )}
        </div>

        {/* 버튼 영역 */}
        <div className="flex-shrink-0 flex items-center gap-2 flex-col sm:flex-col">
          <button
            className="w-full sm:w-auto px-6 py-3 bg-sky-400 hover:bg-sky-500 text-white rounded-xl font-bold transition-all shadow-md flex items-center justify-center gap-2"
          >
            기록하기
          </button>
          <button
            onClick={(e) => { e.stopPropagation(); onAskAi(); }}
            className="w-full sm:w-auto px-6 py-3 bg-indigo-400 hover:bg-indigo-500 text-white rounded-xl font-bold transition-all shadow-md flex items-center justify-center gap-2"
          >
            <MessageCircle className="w-4 h-4" />
            AI에게 물어보기
          </button>
        </div>
      </div>
    </div>
  );
}

export default BookSearchApp;