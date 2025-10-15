package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.exception.ReadingRecordNotFoundException
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import com.yesjm.readlog.domain.model.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReadingRecordService(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : CreateReadingRecordUseCase, GetReadingRecordsUseCase {

    @Transactional
    override fun create(command: CreateReadingRecordCommand): ReadingRecordResponse {
        val book = bookRepository.findById(command.bookId)
            ?: throw BookNotFoundException(command.bookId)

        val record = ReadingRecord(
            id = null,
            book = book,
            rating = Rating(command.rating),
            readingPeriod = ReadingPeriod(command.startDate, command.endDate),
            review = command.review,
            status = ReadingStatus.valueOf(command.status)
        )

        val saved =  readingRecordRepository.save(record)
        return ReadingRecordResponse.from(saved)
    }

    override fun getById(id: Long): ReadingRecordResponse {
        val record = readingRecordRepository.findById(id)
            ?: throw ReadingRecordNotFoundException(id)
        return ReadingRecordResponse.from(record)
    }

    override fun getAll(): List<ReadingRecordResponse> {
        return readingRecordRepository.findAll()
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getByStatus(status: String): List<ReadingRecordResponse> {
        val readingStatus = try {
            ReadingStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효하지 않은 상태입니다: $status")
        }

        return readingRecordRepository.findByStatus(readingStatus)
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getAllSortedByRating(): List<ReadingRecordResponse> {
        return readingRecordRepository.findAll()
            .sortedByDescending { it.rating.value }
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getAllSortedByDate(): List<ReadingRecordResponse> {
        return readingRecordRepository.findAll()
            .sortedWith(compareByDescending<ReadingRecord> { it.readingPeriod.endDate }
                .thenByDescending { it.createdAt })
            .map { ReadingRecordResponse.from(it) }
    }

    @Transactional
    override fun update(id: Long, request: UpdateReadingRecordRequest): ReadingRecordResponse {
        val existing = readingRecordRepository.findById(id)
            ?: throw ReadingRecordNotFoundException(id)

        val updated = existing.copy(
            rating = request.rating?.let { Rating(it) } ?: existing.rating,
            readingPeriod = ReadingPeriod(
                startDate = request.startDate ?: existing.readingPeriod.startDate,
                endDate = request.endDate ?: existing.readingPeriod.endDate
            ),
            review = request.review ?: existing.review,
            status = request.status?.let { ReadingStatus.valueOf(it) } ?: existing.status,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = readingRecordRepository.save(updated)
        return ReadingRecordResponse.from(saved)
    }

    @Transactional
    override fun delete(id: Long) {
        if (readingRecordRepository.findById(id) == null) {
            throw ReadingRecordNotFoundException(id)
        }
        readingRecordRepository.delete(id)
    }

}
