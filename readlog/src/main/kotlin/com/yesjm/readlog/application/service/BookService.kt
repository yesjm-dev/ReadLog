package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.CreateBookRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.port.input.SearchBookUseCase
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.BookSearchPort
import com.yesjm.readlog.application.service.dto.BookResponse
import com.yesjm.readlog.domain.model.Book
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BookService(
    private val bookRepository: BookRepository,
    private val bookSearchPort: BookSearchPort
) : SearchBookUseCase {
    override fun search(query: String): List<BookResponse> {
        return bookSearchPort.search(query)
            .map { externalBook ->
                bookRepository.findByIsbn(externalBook.isbn ?: "")
                    ?.let { BookResponse.from(it) }
                    ?: BookResponse(
                        id = 0L,
                        title = externalBook.title,
                        author = externalBook.author,
                        isbn = externalBook.isbn,
                        imageUrl = externalBook.imageUrl,
                        publisher = externalBook.publisher,
                        description = externalBook.description
                    )
            }
    }

    @Transactional
    override fun createBook(request: CreateBookRequest): BookResponse {
        // ISBN으로 중복 체크
        if (request.isbn != null) {
            bookRepository.findByIsbn(request.isbn)?.let {
                return BookResponse.from(it)
            }
        }

        val book = Book(
            id = null,
            title = request.title,
            author = request.author,
            isbn = request.isbn,
            imageUrl = request.imageUrl,
            publisher = request.publisher,
            description = request.description
        )

        val saved = bookRepository.save(book)
        return BookResponse.from(saved)
    }

    override fun getById(id: Long): BookResponse {
        val book = bookRepository.findById(id)
            ?: throw BookNotFoundException(id)
        return BookResponse.from(book)
    }
}