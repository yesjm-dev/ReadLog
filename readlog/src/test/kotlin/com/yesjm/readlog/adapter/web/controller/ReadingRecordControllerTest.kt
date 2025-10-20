package com.yesjm.readlog.adapter.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yesjm.readlog.adapter.web.dto.BookInformation
import com.yesjm.readlog.adapter.web.dto.CreateReadingRecordRequest
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDate

@WebMvcTest(ReadingRecordController::class)
class ReadingRecordControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var createUseCase: CreateReadingRecordUseCase

    @MockkBean
    private lateinit var getUseCase: GetReadingRecordsUseCase

    @Test
    fun `새로운 책과 함께 독서 기록을 생성할 수 있다`() {
        val book = BookInformation(
            id = null,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = "https://example.com/image.jpg",
            publisher = "인사이트",
            description = "소프트웨어 구조"
        )

        val request = CreateReadingRecordRequest(
            book = book,
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        val response = ReadingRecordResponse(
            id = 1L,
            bookId = 1L,
            bookTitle = "클린 아키텍처",
            bookAuthor = "로버트 C. 마틴",
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        every { createUseCase.create(any()) } returns response

        // when & then
        mockMvc.perform(
            post("/api/reading-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.bookTitle").value("클린 아키텍처"))
            .andExpect(jsonPath("$.rating").value(5))

        verify(exactly = 1) { createUseCase.create(any()) }
    }

    @Test
    fun `유효하지 않은 평점으로 생성 시 400 에러가 발생한다`() {
        val book = BookInformation(
            id = null,
            title = "클린 아키텍처",
            author = "로버트 C. 마틴",
            isbn = "9788966262472",
            imageUrl = "https://example.com/image.jpg",
            publisher = "인사이트",
            description = "소프트웨어 구조"
        )

        val request = CreateReadingRecordRequest(
            book = book,
            rating = 6,  // 유효하지 않은 평점
            startDate = null,
            endDate = null,
            review = null,
            status = "READING"
        )

        // when & then
        mockMvc.perform(
            post("/api/reading-records")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Validation Failed"))
            .andExpect(jsonPath("$.errors.rating").exists())
    }

    @Test
    fun `독서 기록을 조회할 수 있다`() {
        // given
        val response = ReadingRecordResponse(
            id = 1L,
            bookId = 1L,
            bookTitle = "클린 아키텍처",
            bookAuthor = "로버트 C. 마틴",
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        every { getUseCase.getById(1L) } returns response

        // when & then
        mockMvc.perform(get("/api/reading-records/1"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.bookTitle").value("클린 아키텍처"))
    }

    @Test
    fun `모든 독서 기록을 조회할 수 있다`() {
        // given
        val records = listOf(
            ReadingRecordResponse(
                id = 1L,
                bookId = 1L,
                bookTitle = "클린 아키텍처",
                bookAuthor = "로버트 C. 마틴",
                rating = 5,
                startDate = null,
                endDate = null,
                review = null,
                status = "COMPLETED"
            ),
            ReadingRecordResponse(
                id = 2L,
                bookId = 2L,
                bookTitle = "DDD Start!",
                bookAuthor = "최범균",
                rating = 4,
                startDate = null,
                endDate = null,
                review = null,
                status = "READING"
            )
        )

        every { getUseCase.getAll() } returns records

        // when & then
        mockMvc.perform(get("/api/reading-records"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].bookTitle").value("클린 아키텍처"))
            .andExpect(jsonPath("$[1].bookTitle").value("DDD Start!"))
    }

    @Test
    fun `상태별로 독서 기록을 조회할 수 있다`() {
        // given
        val records = listOf(
            ReadingRecordResponse(
                id = 1L,
                bookId = 1L,
                bookTitle = "클린 아키텍처",
                bookAuthor = "로버트 C. 마틴",
                rating = 5,
                startDate = null,
                endDate = null,
                review = null,
                status = "COMPLETED"
            )
        )

        every { getUseCase.getByStatus("COMPLETED") } returns records

        // when & then
        mockMvc.perform(get("/api/reading-records?status=COMPLETED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("COMPLETED"))
    }

    @Test
    fun `독서 기록을 삭제할 수 있다`() {
        // given
        every { getUseCase.delete(1L) } returns Unit

        // when & then
        mockMvc.perform(delete("/api/reading-records/1"))
            .andExpect(status().isNoContent)

        verify(exactly = 1) { getUseCase.delete(1L) }
    }
}