package com.yesjm.readlog.adapter.web.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import com.yesjm.readlog.adapter.web.dto.BookInformation
import com.yesjm.readlog.adapter.web.dto.CreateReadingRecordRequest
import com.yesjm.readlog.application.port.input.CreateReadingRecordUseCase
import com.yesjm.readlog.application.port.input.GetReadingRecordsUseCase
import com.yesjm.readlog.application.service.dto.ReadingRecordResponse
import com.yesjm.readlog.infrastructure.security.JwtUtil
import io.mockk.every
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
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

    @MockkBean
    private lateinit var jwtUtil: JwtUtil

    private val testUserId = 1L
    private val testToken = "test-jwt-token"

    private fun setupAuth() {
        every { jwtUtil.validateToken(testToken) } returns true
        every { jwtUtil.getUserIdFromToken(testToken) } returns testUserId
        every { jwtUtil.getEmailFromToken(testToken) } returns "test@test.com"
    }

    @Test
    fun `새로운 책과 함께 독서 기록을 생성할 수 있다`() {
        setupAuth()

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
            bookImageUrl = "https://example.com/image.jpg",
            bookDescription = "소프트웨어 구조",
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        every { createUseCase.create(any(), testUserId) } returns response

        mockMvc.perform(
            post("/api/reading-records")
                .header("Authorization", "Bearer $testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.bookTitle").value("클린 아키텍처"))
            .andExpect(jsonPath("$.rating").value(5))
            .andExpect(jsonPath("$.bookDescription").value("소프트웨어 구조"))

        verify(exactly = 1) { createUseCase.create(any(), testUserId) }
    }

    @Test
    fun `유효하지 않은 평점으로 생성 시 400 에러가 발생한다`() {
        setupAuth()

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
            rating = 6,
            startDate = null,
            endDate = null,
            review = null,
            status = "READING"
        )

        mockMvc.perform(
            post("/api/reading-records")
                .header("Authorization", "Bearer $testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
    }

    @Test
    fun `평점 0은 유효하다`() {
        setupAuth()

        val book = BookInformation(
            id = null,
            title = "읽고 싶은 책",
            author = "작가",
            isbn = "1234567890",
            imageUrl = null,
            publisher = null,
            description = null
        )

        val request = CreateReadingRecordRequest(
            book = book,
            rating = 0,
            startDate = null,
            endDate = null,
            review = null,
            status = "WISH"
        )

        val response = ReadingRecordResponse(
            id = 1L,
            bookId = 1L,
            bookTitle = "읽고 싶은 책",
            bookAuthor = "작가",
            bookImageUrl = null,
            bookDescription = null,
            rating = 0,
            startDate = null,
            endDate = null,
            review = null,
            status = "WISH"
        )

        every { createUseCase.create(any(), testUserId) } returns response

        mockMvc.perform(
            post("/api/reading-records")
                .header("Authorization", "Bearer $testToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.status").value("WISH"))
            .andExpect(jsonPath("$.rating").value(0))
    }

    @Test
    fun `독서 기록을 조회할 수 있다`() {
        setupAuth()

        val response = ReadingRecordResponse(
            id = 1L,
            bookId = 1L,
            bookTitle = "클린 아키텍처",
            bookAuthor = "로버트 C. 마틴",
            bookImageUrl = null,
            bookDescription = "소프트웨어 구조",
            rating = 5,
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 1, 15),
            review = "좋은 책입니다",
            status = "COMPLETED"
        )

        every { getUseCase.getById(1L, testUserId) } returns response

        mockMvc.perform(
            get("/api/reading-records/1")
                .header("Authorization", "Bearer $testToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.bookTitle").value("클린 아키텍처"))
            .andExpect(jsonPath("$.bookDescription").value("소프트웨어 구조"))
    }

    @Test
    fun `모든 독서 기록을 조회할 수 있다`() {
        setupAuth()

        val records = listOf(
            ReadingRecordResponse(
                id = 1L,
                bookId = 1L,
                bookTitle = "클린 아키텍처",
                bookAuthor = "로버트 C. 마틴",
                bookImageUrl = null,
                bookDescription = null,
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
                bookImageUrl = null,
                bookDescription = null,
                rating = 4,
                startDate = null,
                endDate = null,
                review = null,
                status = "READING"
            )
        )

        every { getUseCase.getAll(testUserId) } returns records

        mockMvc.perform(
            get("/api/reading-records")
                .header("Authorization", "Bearer $testToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].bookTitle").value("클린 아키텍처"))
            .andExpect(jsonPath("$[1].bookTitle").value("DDD Start!"))
    }

    @Test
    fun `상태별로 독서 기록을 조회할 수 있다`() {
        setupAuth()

        val records = listOf(
            ReadingRecordResponse(
                id = 1L,
                bookId = 1L,
                bookTitle = "클린 아키텍처",
                bookAuthor = "로버트 C. 마틴",
                bookImageUrl = null,
                bookDescription = null,
                rating = 5,
                startDate = null,
                endDate = null,
                review = null,
                status = "COMPLETED"
            )
        )

        every { getUseCase.getByStatus("COMPLETED", testUserId) } returns records

        mockMvc.perform(
            get("/api/reading-records?status=COMPLETED")
                .header("Authorization", "Bearer $testToken")
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].status").value("COMPLETED"))
    }

    @Test
    fun `독서 기록을 삭제할 수 있다`() {
        setupAuth()

        every { getUseCase.delete(1L, testUserId) } returns Unit

        mockMvc.perform(
            delete("/api/reading-records/1")
                .header("Authorization", "Bearer $testToken")
        )
            .andExpect(status().isNoContent)

        verify(exactly = 1) { getUseCase.delete(1L, testUserId) }
    }
}
