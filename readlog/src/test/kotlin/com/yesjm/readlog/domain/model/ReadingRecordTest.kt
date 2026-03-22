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
            userId = 1L,
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
    fun `평점은 0에서 5 사이여야 한다`() {
        assertThrows<InvalidRatingException> {
            Rating(-1)
        }

        assertThrows<InvalidRatingException> {
            Rating(6)
        }

        assertDoesNotThrow {
            Rating(0)
        }

        assertDoesNotThrow {
            Rating(3)
        }

        assertDoesNotThrow {
            Rating(5)
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

    @Test
    fun `WISH 상태로 독서 기록을 생성할 수 있다`() {
        val book = Book(
            id = null,
            title = "읽고 싶은 책",
            author = "작가",
            isbn = "1234567890"
        )

        val record = ReadingRecord(
            id = null,
            userId = 1L,
            book = book,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(startDate = null, endDate = null),
            review = null,
            status = ReadingStatus.WISH
        )

        assertEquals(ReadingStatus.WISH, record.status)
        assertEquals(0, record.rating.value)
        assertEquals(null, record.readingPeriod.startDate)
    }

    @Test
    fun `WISH 상태의 기록을 완독으로 변경할 수 있다`() {
        val book = Book(
            id = null,
            title = "읽고 싶은 책",
            author = "작가",
            isbn = "1234567890"
        )

        val record = ReadingRecord(
            id = 1L,
            userId = 1L,
            book = book,
            rating = Rating(0),
            readingPeriod = ReadingPeriod(startDate = null, endDate = null),
            review = null,
            status = ReadingStatus.WISH
        )

        val completed = record.complete(
            endDate = LocalDate.of(2025, 3, 15),
            rating = Rating(4),
            review = "좋았습니다"
        )

        assertEquals(ReadingStatus.COMPLETED, completed.status)
        assertEquals(4, completed.rating.value)
        assertEquals("좋았습니다", completed.review)
    }

    @Test
    fun `모든 ReadingStatus 값이 존재한다`() {
        val statuses = ReadingStatus.entries
        assertEquals(4, statuses.size)
        assert(statuses.contains(ReadingStatus.WISH))
        assert(statuses.contains(ReadingStatus.READING))
        assert(statuses.contains(ReadingStatus.COMPLETED))
        assert(statuses.contains(ReadingStatus.DROPPED))
    }

    @Test
    fun `Rating 경계값을 검증한다`() {
        assertDoesNotThrow { Rating(0) }
        assertDoesNotThrow { Rating(1) }
        assertDoesNotThrow { Rating(5) }
        assertThrows<InvalidRatingException> { Rating(-1) }
        assertThrows<InvalidRatingException> { Rating(6) }
        assertThrows<InvalidRatingException> { Rating(100) }
    }
}
