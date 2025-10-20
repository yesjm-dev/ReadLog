package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.ReadingRecordEntity
import com.yesjm.readlog.infrastructure.persistence.entity.ReadingStatusEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaReadingRecordRepository : JpaRepository<ReadingRecordEntity, Long> {
    fun findByStatus(status: ReadingStatusEntity): List<ReadingRecordEntity>
    fun findAllByOrderByEndDateDesc(): List<ReadingRecordEntity>
    fun findAllByOrderByRatingDesc(): List<ReadingRecordEntity>
    fun findByBookId(bookId: Long): List<ReadingRecordEntity>
}