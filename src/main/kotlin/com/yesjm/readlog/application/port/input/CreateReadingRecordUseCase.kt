package com.yesjm.readlog.application.port.input

import com.yesjm.readlog.application.service.dto.CreateReadingRecordCommand
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse

interface CreateReadingRecordUseCase {
    fun create(command: CreateReadingRecordCommand): ReadingRecordResponse
}