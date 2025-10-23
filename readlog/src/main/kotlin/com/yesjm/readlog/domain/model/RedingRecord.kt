package com.yesjm.readlog.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class ReadingRecord(
    val id: Long?,
    val userId: Long,
    val book: Book,
    val rating: Rating,
    val readingPeriod: ReadingPeriod,
    val review: String?,
    val status: ReadingStatus,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
) {
    fun complete(endDate: LocalDate, rating: Rating, review: String?): ReadingRecord {
        return copy(
            status = ReadingStatus.COMPLETED,
            readingPeriod = readingPeriod.copy(endDate = endDate),
            rating = rating,
            review = review,
            updatedAt = LocalDateTime.now()
        )
    }

    fun isCompleted() = status == ReadingStatus.COMPLETED
}

enum class ReadingStatus {
    READING,
    COMPLETED,
    DROPPED
}