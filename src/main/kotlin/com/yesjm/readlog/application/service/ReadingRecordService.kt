package com.yesjm.readlog.application.service

import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.application.port.output.ReadingRecordRepository
import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import com.yesjm.readlog.domain.model.*
import org.springframework.stereotype.Service

@Service
class ReadingRecordService(
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository
) : CreateReadingRecordUseCase {
    override fun create(command: CreateReadingRecordCommand): ReadingRecordResponse {
        val book = bookRepository.findById(command.bookId)
            ?: throw IllegalArgumentException("책을 찾을 수 없습니다: ${command.bookId}")

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

}
