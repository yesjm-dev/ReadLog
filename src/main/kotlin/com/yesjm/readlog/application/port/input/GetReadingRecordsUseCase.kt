package com.yesjm.readlog.application.port.input

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse

interface GetReadingRecordsUseCase {
    fun getById(id: Long): ReadingRecordResponse
    fun getAll(): List<ReadingRecordResponse>
    fun getByStatus(status: String): List<ReadingRecordResponse>
    fun getAllSortedByRating(): List<ReadingRecordResponse>
    fun getAllSortedByDate(): List<ReadingRecordResponse>
    fun update(id: Long, request: UpdateReadingRecordRequest): ReadingRecordResponse
    fun delete(id: Long)
}