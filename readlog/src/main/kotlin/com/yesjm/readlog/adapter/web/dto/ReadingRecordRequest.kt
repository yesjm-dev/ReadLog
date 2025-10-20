package com.yesjm.readlog.adapter.web.dto

import com.yesjm.readlog.application.service.dto.BookInformationCommand
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

data class CreateReadingRecordRequest(
    @field:NotNull(message = "책 정보는 필수입니다")
    val book: BookInformation,
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
            bookInformation = BookInformationCommand(
                id = book.id,
                title = book.title,
                author = book.author,
                isbn = book.isbn,
                imageUrl = book.imageUrl,
                publisher = book.publisher,
                description = book.description
            ),
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

data class BookInformation(
    val id: Long?,  // 이미 저장된 책이면 id 전달

    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,

    @field:NotBlank(message = "저자는 필수입니다")
    val author: String,

    val isbn: String?,
    val imageUrl: String?,
    val publisher: String?,
    val description: String?
)