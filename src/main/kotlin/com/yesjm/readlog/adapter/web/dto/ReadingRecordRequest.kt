package com.yesjm.readlog.adapter.web.dto

import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateReadingRecordRequest(
    @field:NotNull(message = "책 ID는 필수입니다")
    val bookId: Long,

    @field:NotNull(message = "평점은 필수입니다")
    @field:Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @field:Max(value = 5, message = "평점은 5 이하여야 합니다")
    val rating: Int,

    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val review: String?,

    @field:NotNull(message = "상태는 필수입니다")
    val status: String
) {
    fun toCommand(): CreateReadingRecordCommand {
        return CreateReadingRecordCommand(
            bookId = bookId,
            rating = rating,
            startDate = startDate,
            endDate = endDate,
            review = review,
            status = status
        )
    }
}

data class UpdateReadingRecordRequest(
    @field:Min(value = 1, message = "평점은 1 이상이어야 합니다")
    @field:Max(value = 5, message = "평점은 5 이하여야 합니다")
    val rating: Int?,

    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val review: String?,
    val status: String?
)