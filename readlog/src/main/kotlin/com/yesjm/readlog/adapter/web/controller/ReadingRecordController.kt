package com.yesjm.readlog.adapter.web.controller

import com.yesjm.readlog.adapter.web.dto.CreateReadingRecordRequest
import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reading-records")
class ReadingRecordController(
    private val createReadingRecordUseCase: CreateReadingRecordUseCase,
    private val getReadingRecordsUseCase: GetReadingRecordsUseCase
) {

    @PostMapping
    fun createReadingRecord(
        @Valid @RequestBody request: CreateReadingRecordRequest
    ): ResponseEntity<ReadingRecordResponse> {
        val response = createReadingRecordUseCase.create(request.toCommand())
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getReadingRecord(@PathVariable id: Long): ResponseEntity<ReadingRecordResponse> {
        val response = getReadingRecordsUseCase.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllReadingRecords(
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) sortBy: String?
    ): ResponseEntity<List<ReadingRecordResponse>> {
        val records = when {
            status != null -> getReadingRecordsUseCase.getByStatus(status)
            sortBy == "rating" -> getReadingRecordsUseCase.getAllSortedByRating()
            sortBy == "date" -> getReadingRecordsUseCase.getAllSortedByDate()
            else -> getReadingRecordsUseCase.getAll()
        }
        return ResponseEntity.ok(records)
    }

    @PutMapping("/{id}")
    fun updateReadingRecord(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateReadingRecordRequest
    ): ResponseEntity<ReadingRecordResponse> {
        val response = getReadingRecordsUseCase.update(id, request)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteReadingRecord(@PathVariable id: Long): ResponseEntity<Void> {
        getReadingRecordsUseCase.delete(id)
        return ResponseEntity.noContent().build()
    }

}