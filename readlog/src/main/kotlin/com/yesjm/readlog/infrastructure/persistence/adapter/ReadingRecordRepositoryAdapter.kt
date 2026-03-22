package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.domain.model.ReadingRecord
import com.yesjm.readlog.domain.model.ReadingStatus
import com.yesjm.readlog.infrastructure.persistence.entity.ReadingStatusEntity
import com.yesjm.readlog.infrastructure.persistence.mapper.ReadingRecordMapper
import com.yesjm.readlog.infrastructure.persistence.repository.JpaBookRepository
import com.yesjm.readlog.infrastructure.persistence.repository.JpaReadingRecordRepository
import jakarta.transaction.Transactional
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
@Transactional
class ReadingRecordRepositoryAdapter(
    private val jpaReadingRecordRepository: JpaReadingRecordRepository,
    private val jpaBookRepository: JpaBookRepository
) : ReadingRecordRepository {
    override fun save(record: ReadingRecord): ReadingRecord {
        val bookEntity = jpaBookRepository.findByIdOrNull(record.book.id!!)
            ?: throw IllegalArgumentException("책을 찾을 수 없습니다: ${record.book.id}")

        val entity = if (record.id != null) {
            jpaReadingRecordRepository.findByIdOrNull(record.id)?.also {
                ReadingRecordMapper.updateEntity(it, record)
            } ?: ReadingRecordMapper.toEntity(record, bookEntity)
        } else {
            ReadingRecordMapper.toEntity(record, bookEntity)
        }

        val saved = jpaReadingRecordRepository.save(entity)
        return ReadingRecordMapper.toDomain(saved)
    }

    override fun findById(id: Long): ReadingRecord? {
        return jpaReadingRecordRepository.findByIdOrNull(id)?.let {
            ReadingRecordMapper.toDomain(it)
        }
    }

    override fun findByUserId(userId: Long): List<ReadingRecord> {
        return jpaReadingRecordRepository.findByUserId(userId).map {
            ReadingRecordMapper.toDomain(it)
        }
    }

    override fun findByUserIdAndStatus(userId: Long, status: ReadingStatus): List<ReadingRecord> {
        val entityStatus = when (status) {
            ReadingStatus.WISH -> ReadingStatusEntity.WISH
            ReadingStatus.READING -> ReadingStatusEntity.READING
            ReadingStatus.COMPLETED -> ReadingStatusEntity.COMPLETED
            ReadingStatus.DROPPED -> ReadingStatusEntity.DROPPED
        }
        return jpaReadingRecordRepository.findByUserIdAndStatus(userId, entityStatus).map {
            ReadingRecordMapper.toDomain(it)
        }
    }

    override fun delete(id: Long) {
        jpaReadingRecordRepository.deleteById(id)
    }
}