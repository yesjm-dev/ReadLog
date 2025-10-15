package com.yesjm.readlog.adapter.web.dto

import jakarta.validation.constraints.NotBlank

data class CreateBookRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    val title: String,

    @field:NotBlank(message = "저자는 필수입니다")
    val author: String,

    val isbn: String?,
    val imageUrl: String?,
    val publisher: String?,
    val description: String?
)
