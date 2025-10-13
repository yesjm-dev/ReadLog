package com.yesjm.readlog.application.service

import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.domain.model.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import java.time.LocalDate
import kotlin.test.assertEquals

class ReadingRecordServiceTest {
    private val readingRecordRepository: ReadingRecordRepository = mockk()
    private val bookRepository: BookRepository = mockk()
    private val service = ReadingRecordService(readingRecordRepository, bookRepository)

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
}