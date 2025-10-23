package com.yesjm.readlog.adapter.web.controller

import com.yesjm.readlog.adapter.web.dto.CreateReadingRecordRequest
import com.yesjm.readlog.adapter.web.dto.UpdateReadingRecordRequest
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import com.yesjm.readlog.infrastructure.security.JwtAuthentication
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reading-records")
class ReadingRecordController(
    private val createReadingRecordUseCase: CreateReadingRecordUseCase,
    private val getReadingRecordsUseCase: GetReadingRecordsUseCase
) {

    @PostMapping
    fun createReadingRecord(
        @Valid @RequestBody request: CreateReadingRecordRequest,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ReadingRecordResponse> {
        val response = createReadingRecordUseCase.create(request.toCommand(), jwtAuth.userId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getReadingRecord(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ReadingRecordResponse> {
        val response = getReadingRecordsUseCase.getById(id, jwtAuth.userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllReadingRecords(
        @RequestParam(required = false) status: String?,
        @RequestParam(required = false) sortBy: String?,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<List<ReadingRecordResponse>> {
        val records = when {
            status != null -> getReadingRecordsUseCase.getByStatus(status, jwtAuth.userId)
            sortBy == "rating" -> getReadingRecordsUseCase.getAllSortedByRating(jwtAuth.userId)
            sortBy == "date" -> getReadingRecordsUseCase.getAllSortedByDate(jwtAuth.userId)
            else -> getReadingRecordsUseCase.getAll(jwtAuth.userId)
        }
        return ResponseEntity.ok(records)
    }

    @PutMapping("/{id}")
    fun updateReadingRecord(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateReadingRecordRequest,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ReadingRecordResponse> {
        val response = getReadingRecordsUseCase.update(id, request, jwtAuth.userId)
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteReadingRecord(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<Void> {
        getReadingRecordsUseCase.delete(id, jwtAuth.userId)
        return ResponseEntity.noContent().build()
    }

}