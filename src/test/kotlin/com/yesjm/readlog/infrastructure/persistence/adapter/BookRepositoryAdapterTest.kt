package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.domain.model.Book
import com.yesjm.readlog.infrastructure.persistence.repository.JpaBookRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import

@DataJpaTest
@Import(BookRepositoryAdapter::class)
class BookRepositoryAdapterTest {

    @Autowired
    private lateinit var adapter: BookRepositoryAdapter

    @Autowired
    private lateinit var jpaBookRepository: JpaBookRepository

    @Test
    fun `책을 저장하고 조회할 수 있다`() {
        val book = Book(
            id = null,
            title = "클린 아키텍쳐",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = "https://example.com/image.jpg",
            publisher = "인사이트",
            description = "소프트웨어 구조와 설계의 원칙"
        )

        val saved = adapter.save(book)
        val found = adapter.findById(saved.id!!)

        assertNotNull(saved.id)
        assertEquals("클린 아키텍쳐", found?.title)
        assertEquals("로버트 C. 마틴", found?.author)
        assertEquals("9788966262472", found?.isbn)
    }

    @Test
    fun `ISBN으로 책을 찾을 수 있다`() {
        val book = Book(
            id = null,
            title = "가상 면접 사례로 배우는 대규모 시스템 설계 기초",
            author = "알렉스 쉬",
            isbn = "9788966263158"
        )
        adapter.save(book)

        val found = adapter.findByIsbn("9788966263158")

        assertNotNull(found)
        assertEquals("가상 면접 사례로 배우는 대규모 시스템 설계 기초", found!!.title)
    }

    @Test
    fun `존재하지 않는 책을 조회하면 null을 반환한다`() {
        val found = adapter.findById(999L)

        assertNull(found)
    }

    @Test
    fun `책을 수정할 수 있다`() {
        val book = Book(
            id = null,
            title = "Original Title",
            author = "Original Author",
            isbn = "1234567890"
        )
        val saved = adapter.save(book)

        val updated = saved.copy(title = "Updated Title")
        adapter.save(updated)
        val found = adapter.findById(saved.id!!)

        assertEquals("Updated Title", found?.title)
        assertEquals("Original Author", found?.author)
    }
}