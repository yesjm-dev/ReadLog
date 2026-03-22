package com.yesjm.readlog.application.service

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.exception.ReadingRecordNotFoundException
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.application.service.dto.BookInformationCommand
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import com.yesjm.readlog.domain.model.*
import com.yesjm.readlog.infrastructure.security.SecurityUtil
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ReadingRecordService(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository,
) : CreateReadingRecordUseCase, GetReadingRecordsUseCase {

    @Transactional
    override fun create(command: CreateReadingRecordCommand, userId: Long): ReadingRecordResponse {
        val book = getOrCreateBook(command.bookInformation)

        val record = ReadingRecord(
            id = null,
            userId = userId,
            book = book,
            rating = Rating(command.rating),
            readingPeriod = ReadingPeriod(command.startDate, command.endDate),
            review = command.review,
            status = ReadingStatus.valueOf(command.status)
        )

        val saved = readingRecordRepository.save(record)
        return ReadingRecordResponse.from(saved)
    }

    /**
     * 책을 가져오거나 새로 생성
     * 1. id가 있으면 → 기존 책 조회
     * 2. ISBN이 있으면 → ISBN으로 조회 후 없으면 새로 생성
     * 3. 둘 다 없으면 → 무조건 새로 생성
     */
    private fun getOrCreateBook(bookInformation: BookInformationCommand): Book {
        if (bookInformation.id != null) {
            return bookRepository.findById(bookInformation.id)
                ?: throw BookNotFoundException(bookInformation.id)
        }

        if (!bookInformation.isbn.isNullOrBlank()) {
            bookRepository.findByIsbn(bookInformation.isbn)?.let { existingBook ->
                return existingBook
            }
        }

        val newBook = Book(
            id = null,
            title = bookInformation.title,
            author = bookInformation.author,
            isbn = bookInformation.isbn,
            imageUrl = bookInformation.imageUrl,
            publisher = bookInformation.publisher,
            description = bookInformation.description
        )

        return bookRepository.save(newBook)
    }

    override fun getById(id: Long, userId: Long): ReadingRecordResponse {
        val record = readingRecordRepository.findById(id)
            ?: throw ReadingRecordNotFoundException(id)
        return ReadingRecordResponse.from(record)
    }

    override fun getAll(userId: Long): List<ReadingRecordResponse> {
        return readingRecordRepository.findByUserId(userId)
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getByStatus(status: String, userId: Long): List<ReadingRecordResponse> {
        val readingStatus = try {
            ReadingStatus.valueOf(status.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("유효하지 않은 상태입니다: $status")
        }

        return readingRecordRepository.findByUserIdAndStatus(userId, readingStatus)
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getAllSortedByRating(userId: Long): List<ReadingRecordResponse> {
        return readingRecordRepository.findByUserId(userId)
            .sortedByDescending { it.rating.value }
            .map { ReadingRecordResponse.from(it) }
    }

    override fun getAllSortedByDate(userId: Long): List<ReadingRecordResponse> {
        return readingRecordRepository.findByUserId(userId)
            .sortedWith(compareByDescending<ReadingRecord> { it.readingPeriod.endDate }
                .thenByDescending { it.createdAt })
            .map { ReadingRecordResponse.from(it) }
    }

    @Transactional
    override fun update(id: Long, request: UpdateReadingRecordRequest, userId: Long): ReadingRecordResponse {
        val existing = readingRecordRepository.findById(id)
            ?: throw ReadingRecordNotFoundException(id)

        val newStatus = request.status?.let { ReadingStatus.valueOf(it) } ?: existing.status
        val newRating = request.rating?.let { Rating(it) } ?: existing.rating

        if (newStatus == ReadingStatus.COMPLETED && newRating.value == 0) {
            throw IllegalArgumentException("완독 시 평점을 입력해주세요.")
        }

        val updated = existing.copy(
            rating = newRating,
            readingPeriod = ReadingPeriod(
                startDate = request.startDate ?: existing.readingPeriod.startDate,
                endDate = request.endDate ?: existing.readingPeriod.endDate
            ),
            review = request.review ?: existing.review,
            status = newStatus,
            updatedAt = java.time.LocalDateTime.now()
        )

        val saved = readingRecordRepository.save(updated)
        return ReadingRecordResponse.from(saved)
    }

    @Transactional
    override fun delete(id: Long, userId: Long) {
        if (readingRecordRepository.findById(id) == null) {
            throw ReadingRecordNotFoundException(id)
        }
        readingRecordRepository.delete(id)
    }

}
