package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.ReadingRecord
import com.yesjm.readlog.domain.model.ReadingStatus

interface ReadingRecordRepository {
    fun save(record: ReadingRecord): ReadingRecord
    fun findById(id: Long): ReadingRecord?
    fun findAll(): List<ReadingRecord>
    fun findByStatus(status: ReadingStatus): List<ReadingRecord>
    fun delete(id: Long)
}