package com.yesjm.readlog.domain.model

import java.time.LocalDate
import java.time.temporal.ChronoUnit

data class ReadingPeriod(
    val startDate: LocalDate?,
    val endDate: LocalDate?
) {
    init {
        if (startDate != null && endDate != null) {
            require(endDate >= startDate) {
                "종료일은 시작일보다 늦어야 합니다"
            }
        }
    }

    fun durationInDays(): Long? {
        return if (startDate != null && endDate != null) {
            ChronoUnit.DAYS.between(startDate, endDate)
        } else {
            null
        }
    }
}