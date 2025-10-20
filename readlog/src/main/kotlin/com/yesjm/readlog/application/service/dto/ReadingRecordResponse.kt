package com.yesjm.readlog.application.service.dto

import com.yesjm.readlog.domain.model.ReadingRecord
import java.time.LocalDate

data class ReadingRecordResponse(
    val id: Long,
    val bookId: Long,
    val bookTitle: String,
    val bookAuthor: String?,
    val bookImageUrl: String?,
    val rating: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val review: String?,
    val status: String
) {
    companion object {
        fun from(record: ReadingRecord): ReadingRecordResponse {
            return ReadingRecordResponse(
                id = record.id!!,
                bookId = record.book.id!!,
                bookTitle = record.book.title,
                bookAuthor = record.book.author,
                bookImageUrl = record.book.imageUrl,
                rating = record.rating.value,
                startDate = record.readingPeriod.startDate,
                endDate = record.readingPeriod.endDate,
                review = record.review,
                status = record.status.name
            )
        }
    }
}