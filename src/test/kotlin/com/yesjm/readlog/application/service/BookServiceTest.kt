package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.CreateBookRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.BookSearchPort
import com.yesjm.readlog.domain.model.Book
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class BookServiceTest {

    private val bookRepository: BookRepository = mockk()
    private val bookSearchPort: BookSearchPort = mockk()
    private val service: BookService = BookService(bookRepository, bookSearchPort)


    @Test
    fun `책을 생성할 수 있다`() {
        val request = CreateBookRequest(
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = "https://example.com/image.jpg",
            publisher = "인사이트",
            description = "소프트웨어 구조"
        )

        val book = Book(
            id = 1L,
            title = request.title,
            author = request.author,
            isbn = request.isbn,
            imageUrl = request.imageUrl,
            publisher = request.publisher,
            description = request.description
        )

        every { bookRepository.findByIsbn("9788966262472") } returns null
        every { bookRepository.save(any()) } returns book

        val result = service.createBook(request)

        assertEquals("클린 아키텍처", result.title)
        assertEquals("로버트 C. 마틴", result.author)
        verify(exactly = 1) { bookRepository.save(any()) }
    }

    @Test
    fun `이미 존재하는 ISBN의 책 생성 시 기존 책을 반환한다`() {
        val request = CreateBookRequest(
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = null,
            publisher = null,
            description = null
        )

        val existingBook = Book(
            id = 1L,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472"
        )

        every { bookRepository.findByIsbn("9788966262472") } returns existingBook

        val result = service.createBook(request)

        assertEquals(1L, result.id)
        verify(exactly = 0) { bookRepository.save(any()) }
    }

    @Test
    fun `책을 ID로 조회할 수 있다`() {
        val book = Book(
            id = 1L,
            title = "DDD Start!",
            author = "최범균",
            isbn = "9788960777330"
        )

        every { bookRepository.findById(1L) } returns book

        val result = service.getById(1L)

        assertEquals("DDD Start!", result.title)
    }

    @Test
    fun `존재하지 않는 책 조회 시 예외가 발생한다`() {
        every { bookRepository.findById(999L) } returns null

        assertThrows<BookNotFoundException> {
            service.getById(999L)
        }
    }

    @Test
    fun `외부 API로 책을 검색할 수 있다`() {
        val searchResults = listOf(
            Book(
                id = null,
                title = "클린 코드",
                author = "로버트 C. 마틴",
                isbn = "9788966260959"
            ),
            Book(
                id = null,
                title = "클린 아키텍처",
                author = "로버트 C. 마틴",
                isbn = "9788966262472"
            )
        )

        every { bookSearchPort.search("클린") } returns searchResults
        every { bookRepository.findByIsbn(any()) } returns null

        val result = service.search("클린")

        assertEquals(2, result.size)
        assertEquals("클린 코드", result[0].title)
        verify(exactly = 1) { bookSearchPort.search("클린") }
    }

}