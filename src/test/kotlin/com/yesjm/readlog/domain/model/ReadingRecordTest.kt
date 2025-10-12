package com.yesjm.readlog.domain.model

import com.yesjm.readlog.domain.exception.InvalidRatingException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import kotlin.test.assertEquals

class ReadingRecordTest {
    @Test
    fun `독서 기록을 생성할 수 있다`() {
        val book = Book(
            id = null,
            title = "클린 아키텍쳐",
            author = "로버트 C. 마틴",
            isbn = "9788966262472"
        )

        val record = ReadingRecord(
            id = null,
            book = book,
            rating = Rating(5),
            readingPeriod = ReadingPeriod(
                startDate = LocalDate.of(2025, 1, 1),
                endDate = LocalDate.of(2025, 1, 31)
            ),
            review = "좋은 책입니다.",
            status = ReadingStatus.COMPLETED
        )

        assertEquals("클린 아키텍쳐", record.book.title)
        assertEquals(5, record.rating.value)
        assertEquals(ReadingStatus.COMPLETED, record.status)
    }

    @Test
    fun `평점은 1에서 5 사이여야 한다`() {
        assertThrows<InvalidRatingException> {
            Rating(0)
        }

        assertThrows<InvalidRatingException> {
            Rating(6)
        }

        assertDoesNotThrow {
            Rating(3)
        }
    }

    @Test
    fun `완독한 기록은 독서 기간을 계산할 수 있다`() {
        val period = ReadingPeriod(
            startDate = LocalDate.of(2025, 1, 1),
            endDate = LocalDate.of(2025, 1, 31)
        )

        val days = period.durationInDays()

        assertEquals(30, days)
    }
}