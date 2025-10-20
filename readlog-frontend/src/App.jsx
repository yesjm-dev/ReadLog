import { useState } from 'react'
import BookSearchApp from './BookSearchApp'
import BookshelfPage from './BookshelfPage'

function App() {
  const [currentPage, setCurrentPage] = useState('bookshelf') // 'bookshelf' or 'search'

  return (
    <>
      {currentPage === 'bookshelf' && (
        <BookshelfPage 
          onAddBook={() => setCurrentPage('search')} 
        />
      )}
      
      {currentPage === 'search' && (
        <BookSearchApp 
          onGoToBookshelf={() => setCurrentPage('bookshelf')} 
        />
      )}
    </>
  )
}

export default App