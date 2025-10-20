package com.yesjm.readlog.application.service.dto

import java.time.LocalDate

data class CreateReadingRecordCommand(
    val bookInformation: BookInformationCommand,
    val rating: Int,
    val startDate: LocalDate?,
    val endDate: LocalDate?,
    val review: String?,
    val status: String
)

data class BookInformationCommand(
    val id: Long?,
    val title: String,
    val author: String,
    val isbn: String?,
    val imageUrl: String?,
    val publisher: String?,
    val description: String?
)
