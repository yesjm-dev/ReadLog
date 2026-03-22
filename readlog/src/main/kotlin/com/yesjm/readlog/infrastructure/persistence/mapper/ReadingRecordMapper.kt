package com.yesjm.readlog.infrastructure.persistence.mapper

import com.yesjm.readlog.domain.model.Rating
import com.yesjm.readlog.domain.model.ReadingPeriod
import com.yesjm.readlog.domain.model.ReadingRecord
import com.yesjm.readlog.domain.model.ReadingStatus
import com.yesjm.readlog.infrastructure.persistence.entity.BookEntity
import com.yesjm.readlog.infrastructure.persistence.entity.ReadingRecordEntity
import com.yesjm.readlog.infrastructure.persistence.entity.ReadingStatusEntity

object ReadingRecordMapper {
    fun toDomain(entity: ReadingRecordEntity): ReadingRecord {
        return ReadingRecord(
            id = entity.id,
            userId = entity.userId,
            book = BookMapper.toDomain(entity.book),
            rating = Rating(entity.rating),
            readingPeriod = ReadingPeriod(
                startDate = entity.startDate,
                endDate = entity.endDate
            ),
            review = entity.review,
            status = when (entity.status) {
                ReadingStatusEntity.WISH -> ReadingStatus.WISH
                ReadingStatusEntity.READING -> ReadingStatus.READING
                ReadingStatusEntity.COMPLETED -> ReadingStatus.COMPLETED
                ReadingStatusEntity.DROPPED -> ReadingStatus.DROPPED
            },
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: ReadingRecord, bookEntity: BookEntity): ReadingRecordEntity {
        return ReadingRecordEntity(
            id = domain.id,
            userId = domain.userId,
            book = bookEntity,
            rating = domain.rating.value,
            startDate = domain.readingPeriod.startDate,
            endDate = domain.readingPeriod.endDate,
            review = domain.review,
            status = when (domain.status) {
                ReadingStatus.WISH -> ReadingStatusEntity.WISH
                ReadingStatus.READING -> ReadingStatusEntity.READING
                ReadingStatus.COMPLETED -> ReadingStatusEntity.COMPLETED
                ReadingStatus.DROPPED -> ReadingStatusEntity.DROPPED
            },
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun updateEntity(entity: ReadingRecordEntity, domain: ReadingRecord) {
        entity.rating = domain.rating.value
        entity.startDate = domain.readingPeriod.startDate
        entity.endDate = domain.readingPeriod.endDate
        entity.review = domain.review
        entity.status = when (domain.status) {
            ReadingStatus.WISH -> ReadingStatusEntity.WISH
            ReadingStatus.READING -> ReadingStatusEntity.READING
            ReadingStatus.COMPLETED -> ReadingStatusEntity.COMPLETED
            ReadingStatus.DROPPED -> ReadingStatusEntity.DROPPED
        }
        entity.updatedAt = domain.updatedAt
    }
}