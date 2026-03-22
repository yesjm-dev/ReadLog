package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.exception.ReadingRecordNotFoundException
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.application.service.dto.BookInformationCommand
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.domain.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertEquals

class ReadingRecordServiceTest {
    private val readingRecordRepository: ReadingRecordRepository = mockk()
    private val bookRepository: BookRepository = mockk()
    private val service = ReadingRecordService(readingRecordRepository, bookRepository)

    private lateinit var testBook: Book
    private val testUserId = 1L

    @BeforeEach
    fun setUp() {
        testBook = Book(
            id = 1L,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            description = "소프트웨어 구조"
        )
    }

    @Test
    fun `새로운 책과 함께 독서 기록을 생성할 수 있다`() {
        val command = CreateReadingRecordCommand(
            bookInformation = BookInformationCommand(
                id = null,
                title = "클린 아키텍처",
                author = "로버트 C. 마틴",
                isbn = "9788966262472",
                imageUrl = "https://example.com/image.jpg",
                publisher = "인사이트",
                description = "소프트웨어 구조"
            ),
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        val savedBook = Book(
            id = 1L,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            description = "소프트웨어 구조"
        )

        val savedRecord = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = savedBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(command.startDate, command.endDate),
            review = command.review,
            status = ReadingStatus.COMPLETED,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { bookRepository.findByIsbn("9788966262472") } returns null
        every { bookRepository.save(any()) } returns savedBook
        every { readingRecordRepository.save(any()) } returns savedRecord

        val result = service.create(command, testUserId)

        assertEquals("클린 아키텍처", result.bookTitle)
        assertEquals(5, result.rating)
        verify(exactly = 1) { readingRecordRepository.save(any()) }
    }

    @Test
    fun `WISH 상태로 독서 기록을 생성할 수 있다`() {
        val command = CreateReadingRecordCommand(
            bookInformation = BookInformationCommand(
                id = null,
                title = "읽고 싶은 책",
                author = "작가",
                isbn = "1234567890",
                imageUrl = null,
                publisher = null,
                description = null
            ),
            rating = 0,
            startDate = null,
            endDate = null,
            review = null,
            status = "WISH"
        )

        val savedBook = Book(
            id = 2L,
            title = "읽고 싶은 책",
            author = "작가",
            isbn = "1234567890"
        )

        val savedRecord = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = savedBook,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(null, null),
            review = null,
            status = ReadingStatus.WISH,
            createdAt = LocalDateTime.now(),
            updatedAt = LocalDateTime.now()
        )

        every { bookRepository.findByIsbn("1234567890") } returns null
        every { bookRepository.save(any()) } returns savedBook
        every { readingRecordRepository.save(any()) } returns savedRecord

        val result = service.create(command, testUserId)

        assertEquals("WISH", result.status)
        assertEquals(0, result.rating)
    }

    @Test
    fun `존재하지 않는 책으로 독서 기록 생성 시 예외가 발생한다`() {
        val book = BookInformationCommand(
            id = 999,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = "https://example.com/image.jpg",
            publisher = "인사이트",
            description = "소프트웨어 구조"
        )

        val command = CreateReadingRecordCommand(
            bookInformation = book,
            rating = 5,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            review = "테스트",
            status = "COMPLETED"
        )
        every { bookRepository.findById(999L) } returns null

        assertThrows<BookNotFoundException> {
            service.create(command, testUserId)
        }
    }

    @Test
    fun `독서 기록을 ID로 조회할 수 있다`() {
        val record = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "좋아요",
            status = ReadingStatus.COMPLETED
        )
        every { readingRecordRepository.findById(1L) } returns record

        val result = service.getById(1L, testUserId)

        assertEquals(1L, result.id)
        assertEquals("클린 아키텍처", result.bookTitle)
    }

    @Test
    fun `존재하지 않는 독서 기록 조회 시 예외가 발생한다`() {
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.getById(999L, testUserId)
        }
    }

    @Test
    fun `모든 독서 기록을 조회할 수 있다`() {
        val records = listOf(
            ReadingRecord(
                id = 1L,
                userId = testUserId,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 2L,
                userId = testUserId,
                book = testBook,
                rating = Rating(4),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.READING
            )
        )

        every { readingRecordRepository.findByUserId(testUserId) } returns records

        val result = service.getAll(testUserId)

        assertEquals(2, result.size)
    }

    @Test
    fun `상태별로 독서 기록을 조회할 수 있다`() {
        val completedRecords = listOf(
            ReadingRecord(
                id = 1L,
                userId = testUserId,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            )
        )

        every { readingRecordRepository.findByUserIdAndStatus(testUserId, ReadingStatus.COMPLETED) } returns completedRecords

        val result = service.getByStatus("COMPLETED", testUserId)

        assertEquals(1, result.size)
        assertEquals("COMPLETED", result[0].status)
    }

    @Test
    fun `WISH 상태로 독서 기록을 필터링할 수 있다`() {
        val wishRecords = listOf(
            ReadingRecord(
                id = 1L,
                userId = testUserId,
                book = testBook,
                rating = Rating(0),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.WISH
            )
        )

        every { readingRecordRepository.findByUserIdAndStatus(testUserId, ReadingStatus.WISH) } returns wishRecords

        val result = service.getByStatus("WISH", testUserId)

        assertEquals(1, result.size)
        assertEquals("WISH", result[0].status)
    }

    @Test
    fun `유효하지 않은 상태로 조회 시 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            service.getByStatus("INVALID_STATUS", testUserId)
        }
    }

    @Test
    fun `평점순으로 정렬된 독서 기록을 조회할 수 있다`() {
        val records = listOf(
            ReadingRecord(
                id = 1L,
                userId = testUserId,
                book = testBook,
                rating = Rating(3),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 2L,
                userId = testUserId,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 3L,
                userId = testUserId,
                book = testBook,
                rating = Rating(4),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            )
        )

        every { readingRecordRepository.findByUserId(testUserId) } returns records

        val result = service.getAllSortedByRating(testUserId)

        assertEquals(3, result.size)
        assertEquals(5, result[0].rating)
        assertEquals(4, result[1].rating)
        assertEquals(3, result[2].rating)
    }

    @Test
    fun `독서 기록을 수정할 수 있다`() {
        val existing = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(3),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = "원래 후기",
            status = ReadingStatus.READING
        )

        val request = UpdateReadingRecordRequest(
            rating = 5,
            startDate = null,
            endDate = LocalDate.now(),
            review = "수정된 후기",
            status = "COMPLETED"
        )

        val updated = existing.copy(
            rating = Rating(5),
            readingPeriod = existing.readingPeriod.copy(endDate = LocalDate.now()),
            review = "수정된 후기",
            status = ReadingStatus.COMPLETED
        )

        every { readingRecordRepository.findById(1L) } returns existing
        every { readingRecordRepository.save(any()) } returns updated

        val result = service.update(1L, request, testUserId)

        assertEquals(5, result.rating)
        assertEquals("수정된 후기", result.review)
        assertEquals("COMPLETED", result.status)
        assertNotNull(result.endDate)
    }

    @Test
    fun `완독 시 평점이 0이면 예외가 발생한다`() {
        val existing = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = null,
            status = ReadingStatus.READING
        )

        val request = UpdateReadingRecordRequest(
            rating = 0,
            startDate = null,
            endDate = LocalDate.now(),
            review = null,
            status = "COMPLETED"
        )

        every { readingRecordRepository.findById(1L) } returns existing

        assertThrows<IllegalArgumentException> {
            service.update(1L, request, testUserId)
        }
    }

    @Test
    fun `완독 시 평점이 없고 기존 평점이 0이면 예외가 발생한다`() {
        val existing = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = null,
            status = ReadingStatus.WISH
        )

        val request = UpdateReadingRecordRequest(
            rating = null,
            startDate = null,
            endDate = LocalDate.now(),
            review = null,
            status = "COMPLETED"
        )

        every { readingRecordRepository.findById(1L) } returns existing

        assertThrows<IllegalArgumentException> {
            service.update(1L, request, testUserId)
        }
    }

    @Test
    fun `완독 시 평점이 1 이상이면 성공한다`() {
        val existing = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            review = null,
            status = ReadingStatus.READING
        )

        val request = UpdateReadingRecordRequest(
            rating = 4,
            startDate = null,
            endDate = LocalDate.now(),
            review = "완독 후기",
            status = "COMPLETED"
        )

        val updated = existing.copy(
            rating = Rating(4),
            readingPeriod = existing.readingPeriod.copy(endDate = LocalDate.now()),
            review = "완독 후기",
            status = ReadingStatus.COMPLETED
        )

        every { readingRecordRepository.findById(1L) } returns existing
        every { readingRecordRepository.save(any()) } returns updated

        val result = service.update(1L, request, testUserId)

        assertEquals(4, result.rating)
        assertEquals("COMPLETED", result.status)
    }

    @Test
    fun `WISH에서 READING으로 상태 변경 시 평점 0 허용`() {
        val existing = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(null, null),
            review = null,
            status = ReadingStatus.WISH
        )

        val request = UpdateReadingRecordRequest(
            rating = null,
            startDate = LocalDate.now(),
            endDate = null,
            review = null,
            status = "READING"
        )

        val updated = existing.copy(
            readingPeriod = ReadingPeriod(LocalDate.now(), null),
            status = ReadingStatus.READING
        )

        every { readingRecordRepository.findById(1L) } returns existing
        every { readingRecordRepository.save(any()) } returns updated

        val result = service.update(1L, request, testUserId)

        assertEquals("READING", result.status)
        assertEquals(0, result.rating)
    }

    @Test
    fun `존재하지 않는 독서 기록 수정 시 예외가 발생한다`() {
        val request = UpdateReadingRecordRequest(rating = 5, null, null, null, null)
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.update(999L, request, testUserId)
        }
    }

    @Test
    fun `독서 기록을 삭제할 수 있다`() {
        val record = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(null, null),
            review = null,
            status = ReadingStatus.COMPLETED
        )

        every { readingRecordRepository.findById(1L) } returns record
        every { readingRecordRepository.delete(1L) } returns Unit

        service.delete(1L, testUserId)

        verify(exactly = 1) { readingRecordRepository.delete(1L) }
    }

    @Test
    fun `존재하지 않는 독서 기록 삭제 시 예외가 발생한다`() {
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.delete(999L, testUserId)
        }
    }

    @Test
    fun `ReadingRecordResponse에 bookDescription이 포함된다`() {
        val bookWithDesc = Book(
            id = 1L,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            description = "소프트웨어 구조와 설계의 원칙"
        )

        val record = ReadingRecord(
            id = 1L,
            userId = testUserId,
            book = bookWithDesc,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "좋아요",
            status = ReadingStatus.COMPLETED
        )
        every { readingRecordRepository.findById(1L) } returns record

        val result = service.getById(1L, testUserId)

        assertEquals("소프트웨어 구조와 설계의 원칙", result.bookDescription)
    }
}
