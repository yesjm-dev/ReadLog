package com.yesjm.readlog.application.service.dto

import java.time.LocalDate

data class CreateReadingRecordCommand(
    val bookId: Long,
    val rating: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val review: String?,
    val status: String
)