package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.domain.model.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.context.annotation.Import
import java.time.LocalDate
import kotlin.test.assertEquals

@DataJpaTest
@Import(ReadingRecordRepositoryAdapter::class, BookRepositoryAdapter::class)
class ReadingRecordRepositoryAdapterTest {

    @Autowired
    private lateinit var adapter: ReadingRecordRepositoryAdapter

    @Autowired
    private lateinit var bookAdapter: BookRepositoryAdapter

    private lateinit var testBook: Book

    @BeforeEach
    fun setUp() {
        testBook = bookAdapter.save(
            Book(
                id = null,
                title = "테스트 주도 개발",
                author = "켄트 벡",
                isbn = "9788966261024"
            )
        )
    }


    @Test
    fun `독서 기록을 저장하고 조회할 수 있다`() {
        val record = ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(
                startDate = LocalDate.of(2024, 1, 1),
                endDate = LocalDate.of(2024, 1, 15)
            ),
            review = "TDD의 기초를 잘 배웠습니다",
            status = ReadingStatus.COMPLETED
        )

        val saved = adapter.save(record)
        val found = adapter.findById(saved.id!!)

        assertNotNull(saved.id)
        assertEquals("테스트 주도 개발", found?.book?.title)
        assertEquals(5, found?.rating?.value)
        assertEquals(ReadingStatus.COMPLETED, found?.status)
        assertEquals("TDD의 기초를 잘 배웠습니다", found?.review)
    }


    @Test
    fun `상태별로 독서 기록을 조회할 수 있다`() {
        // given
        adapter.save(ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "완독",
            status = ReadingStatus.COMPLETED
        ))

        adapter.save(ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(3),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = "읽는중",
            status = ReadingStatus.READING
        ))

        // when
        val completed = adapter.findByStatus(ReadingStatus.COMPLETED)
        val reading = adapter.findByStatus(ReadingStatus.READING)

        // then
        assertEquals(1, completed.size)
        assertEquals(1, reading.size)
        assertEquals("완독", completed[0].review)
        assertEquals("읽는중", reading[0].review)
    }

    @Test
    fun `모든 독서 기록을 조회할 수 있다`() {
        // given
        adapter.save(ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "첫번째",
            status = ReadingStatus.COMPLETED
        ))

        adapter.save(ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(4),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = "두번째",
            status = ReadingStatus.READING
        ))

        // when
        val all = adapter.findAll()

        // then
        assertEquals(2, all.size)
    }

    @Test
    fun `독서 기록을 삭제할 수 있다`() {
        // given
        val saved = adapter.save(ReadingRecord(
            id = null,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "삭제될 기록",
            status = ReadingStatus.COMPLETED
        ))

        // when
        adapter.delete(saved.id!!)
        val found = adapter.findById(saved.id!!)

        // then
        assertNull(found)
    }
}