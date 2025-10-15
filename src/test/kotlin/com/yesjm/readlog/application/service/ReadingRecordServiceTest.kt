package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.exception.ReadingRecordNotFoundException
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
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
import kotlin.test.assertEquals

class ReadingRecordServiceTest {
    private val readingRecordRepository: ReadingRecordRepository = mockk()
    private val bookRepository: BookRepository = mockk()
    private val service = ReadingRecordService(readingRecordRepository, bookRepository)

    private lateinit var testBook: Book

    @BeforeEach
    fun setUp() {
        testBook = Book(
            id = 1L,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472"
        )
    }

    @Test
    fun `독서 기록을 생성할 수 있다`() {
        val book = Book(
            id = 1L,
            title = "클린 아키텍쳐",
            author = "로버트 C. 마틴",
            isbn = "9788966262472"
        )

        val command = CreateReadingRecordCommand(
            bookId = 1L,
            rating = 5,
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 1, 31),
            review = "좋은 책입니다.",
            status = "COMPLETED"
        )

        val expectedRecord = ReadingRecord(
            id = null,
            book = book,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(command.startDate, command.endDate),
            review = command.review,
            status = ReadingStatus.COMPLETED
        )

        every { bookRepository.findById(1L) } returns book
        every { readingRecordRepository.save(any()) } returns expectedRecord.copy(id = 1L)

        val result = service.create(command)

        assertEquals("클린 아키텍쳐", result.bookTitle)
        assertEquals(5, result.rating)
        verify(exactly = 1) { readingRecordRepository.save(any()) }
    }


    @Test
    fun `존재하지 않는 책으로 독서 기록 생성 시 예외가 발생한다`() {
        val command = CreateReadingRecordCommand(
            bookId = 999L,
            rating = 5,
            startDate = LocalDate.now(),
            endDate = LocalDate.now(),
            review = "테스트",
            status = "COMPLETED"
        )
        every { bookRepository.findById(999L) } returns null

        assertThrows<BookNotFoundException> {
            service.create(command)
        }
    }

    @Test
    fun `독서 기록을 ID로 조회할 수 있다`() {
        val record = ReadingRecord(
            id = 1L,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(LocalDate.now(), LocalDate.now()),
            review = "좋아요",
            status = ReadingStatus.COMPLETED
        )
        every { readingRecordRepository.findById(1L) } returns record

        val result = service.getById(1L)

        assertEquals(1L, result.id)
        assertEquals("클린 아키텍처", result.bookTitle)
    }

    @Test
    fun `존재하지 않는 독서 기록 조회 시 예외가 발생한다`() {
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.getById(999L)
        }
    }

    @Test
    fun `모든 독서 기록을 조회할 수 있다`() {
        val records = listOf(
            ReadingRecord(
                id = 1L,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 2L,
                book = testBook,
                rating = Rating(4),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.READING
            )
        )

        every { readingRecordRepository.findAll() } returns records

        val result = service.getAll()

        assertEquals(2, result.size)
    }

    @Test
    fun `상태별로 독서 기록을 조회할 수 있다`() {
        val completedRecords = listOf(
            ReadingRecord(
                id = 1L,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            )
        )

        every { readingRecordRepository.findByStatus(ReadingStatus.COMPLETED) } returns completedRecords

        val result = service.getByStatus("COMPLETED")

        assertEquals(1, result.size)
        assertEquals("COMPLETED", result[0].status)
    }

    @Test
    fun `유효하지 않은 상태로 조회 시 예외가 발생한다`() {
        assertThrows<IllegalArgumentException> {
            service.getByStatus("INVALID_STATUS")
        }
    }

    @Test
    fun `평점순으로 정렬된 독서 기록을 조회할 수 있다`() {
        val records = listOf(
            ReadingRecord(
                id = 1L,
                book = testBook,
                rating = Rating(3),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 2L,
                book = testBook,
                rating = Rating(5),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            ),
            ReadingRecord(
                id = 3L,
                book = testBook,
                rating = Rating(4),
                readingPeriod = ReadingPeriod(null, null),
                review = null,
                status = ReadingStatus.COMPLETED
            )
        )

        every { readingRecordRepository.findAll() } returns records

        val result = service.getAllSortedByRating()

        assertEquals(3, result.size)
        assertEquals(5, result[0].rating)
        assertEquals(4, result[1].rating)
        assertEquals(3, result[2].rating)
    }

    @Test
    fun `독서 기록을 수정할 수 있다`() {
        val existing = ReadingRecord(
            id = 1L,
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

        // when
        val result = service.update(1L, request)

        // then
        assertEquals(5, result.rating)
        assertEquals("수정된 후기", result.review)
        assertEquals("COMPLETED", result.status)
        assertNotNull(result.endDate)
    }

    @Test
    fun `존재하지 않는 독서 기록 수정 시 예외가 발생한다`() {
        val request = UpdateReadingRecordRequest(rating = 5, null, null, null, null)
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.update(999L, request)
        }
    }

    @Test
    fun `독서 기록을 삭제할 수 있다`() {
        val record = ReadingRecord(
            id = 1L,
            book = testBook,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(null, null),
            review = null,
            status = ReadingStatus.COMPLETED
        )

        every { readingRecordRepository.findById(1L) } returns record
        every { readingRecordRepository.delete(1L) } returns Unit

        service.delete(1L)

        verify(exactly = 1) { readingRecordRepository.delete(1L) }
    }

    @Test
    fun `존재하지 않는 독서 기록 삭제 시 예외가 발생한다`() {
        every { readingRecordRepository.findById(999L) } returns null

        assertThrows<ReadingRecordNotFoundException> {
            service.delete(999L)
        }
    }
}