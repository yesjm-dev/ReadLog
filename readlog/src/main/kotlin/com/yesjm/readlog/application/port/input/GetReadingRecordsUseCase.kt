package com.yesjm.readlog.application.port.input

import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse

interface GetReadingRecordsUseCase {
    fun getById(id: Long, userId: Long): ReadingRecordResponse
    fun getAll(userId: Long): List<ReadingRecordResponse>
    fun getByStatus(status: String, userId: Long): List<ReadingRecordResponse>
    fun getAllSortedByRating(userId: Long): List<ReadingRecordResponse>
    fun getAllSortedByDate(userId: Long): List<ReadingRecordResponse>
    fun update(id: Long, request: UpdateReadingRecordRequest, userId: Long): ReadingRecordResponse
    fun delete(id: Long, userId: Long)
}