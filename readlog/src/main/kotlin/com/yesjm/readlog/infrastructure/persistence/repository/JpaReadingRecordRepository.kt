package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.ReadingRecordEntity
import com.yesjm.readlog.infrastructure.persistence.entity.ReadingStatusEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaReadingRecordRepository : JpaRepository<ReadingRecordEntity, Long> {
    fun findByUserId(userId: Long): List<ReadingRecordEntity>
    fun findByUserIdAndStatus(userId: Long, status: ReadingStatusEntity): List<ReadingRecordEntity>
    fun findByUserIdOrderByEndDateDesc(userId: Long): List<ReadingRecordEntity>
    fun findByUserIdOrderByRatingDesc(userId: Long): List<ReadingRecordEntity>
}