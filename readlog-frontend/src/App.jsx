// App.jsx
import { useState } from 'react'
import BookSearchApp from './BookSearchApp'
import BookshelfPage from './BookshelfPage'
import BookDetailPage from './BookDetailPage'
import { ToastProvider } from './Toast'

function App() {
  const [currentPage, setCurrentPage] = useState('bookshelf') // 'bookshelf', 'search', 'detail'
  const [selectedRecordId, setSelectedRecordId] = useState(null)

  const handleGoToDetail = (recordId) => {
    setSelectedRecordId(recordId)
    setCurrentPage('detail')
  }

  const handleDeleteFromDetail = (deletedId) => {
    // 상세 페이지에서 삭제 후 책장으로 돌아감
    setCurrentPage('bookshelf')
  }

  return (
    <ToastProvider>
      {currentPage === 'bookshelf' && (
        <BookshelfPage 
          onAddBook={() => setCurrentPage('search')}
          onBookClick={handleGoToDetail}
        />
      )}
      
      {currentPage === 'search' && (
        <BookSearchApp 
          onGoToBookshelf={() => setCurrentPage('bookshelf')} 
        />
      )}

      {currentPage === 'detail' && (
        <BookDetailPage
          recordId={selectedRecordId}
          onBack={() => setCurrentPage('bookshelf')}
          onDelete={handleDeleteFromDetail}
        />
      )}
    </ToastProvider>
  )
}

export default App