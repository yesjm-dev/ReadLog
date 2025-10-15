package com.yesjm.readlog.infrastructure.external.naver

import com.yesjm.readlog.application.exception.ExternalServiceException
import com.yesjm.readlog.infrastructure.external.naver.config.NaverApiProperties
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.web.reactive.function.client.WebClient

class NaverBookSearchAdapterTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var adapter: NaverBookSearchAdapter

    @BeforeEach
    fun setUp() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val properties = NaverApiProperties(
            clientId = "test-client-id",
            clientSecret = "test-client-secret"
        )

        val webClient = WebClient.builder()
            .baseUrl(mockWebServer.url("/").toString())
            .build()

        adapter = NaverBookSearchAdapter(properties, webClient)
    }

    @AfterEach
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `네이버 API로 책을 검색할 수 있다`() {
        val mockResponse = """
            {
              "lastBuildDate": "Mon, 01 Jan 2024 00:00:00 +0900",
              "total": 2,
              "start": 1,
              "display": 2,
              "items": [
                {
                  "title": "<b>클린</b> <b>아키텍처</b>",
                  "link": "https://example.com",
                  "image": "https://example.com/image1.jpg",
                  "author": "<b>로버트 C. 마틴</b>",
                  "discount": "27000",
                  "publisher": "인사이트",
                  "pubdate": "20190828",
                  "isbn": "1234567890 9788966262472",
                  "description": "소프트웨어 구조와 설계의 원칙"
                },
                {
                  "title": "<b>클린</b> 코드",
                  "link": "https://example.com",
                  "image": "https://example.com/image2.jpg",
                  "author": "로버트 C. 마틴",
                  "discount": "29700",
                  "publisher": "인사이트",
                  "pubdate": "20130624",
                  "isbn": "9788966260959",
                  "description": "애자일 소프트웨어 장인 정신"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = adapter.search("클린 아키텍처")

        assertEquals(2, result.size)
        val firstBook = result[0]
        assertEquals("클린 아키텍처", firstBook.title)
        assertEquals("로버트 C. 마틴", firstBook.author)
        assertEquals("9788966262472", firstBook.isbn)
        assertEquals("https://example.com/image1.jpg", firstBook.imageUrl)
        assertEquals("인사이트", firstBook.publisher)
    }

    @Test
    fun `HTML 태그가 제거된다`() {
        val mockResponse = """
            {
              "lastBuildDate": "Mon, 01 Jan 2024 00:00:00 +0900",
              "total": 1,
              "start": 1,
              "display": 1,
              "items": [
                {
                  "title": "<b>테스트</b> 제목",
                  "link": "https://example.com",
                  "image": "",
                  "author": "<b>테스트</b> 저자",
                  "discount": "0",
                  "publisher": "출판사",
                  "pubdate": "20200101",
                  "isbn": "1234567890123",
                  "description": "<b>테스트</b> 설명"
                }
              ]
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = adapter.search("테스트")

        assertEquals("테스트 제목", result[0].title)
        assertEquals("테스트 저자", result[0].author)
        assertEquals("테스트 설명", result[0].description)
    }

    @Test
    fun `API 호출 실패 시 예외가 발생한다`() {
        mockWebServer.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        assertThrows<ExternalServiceException> {
            adapter.search("오류 테스트")
        }
    }

    @Test
    fun `빈 결과를 반환할 수 있다`() {
        val mockResponse = """
            {
              "lastBuildDate": "Mon, 01 Jan 2024 00:00:00 +0900",
              "total": 0,
              "start": 1,
              "display": 0,
              "items": []
            }
        """.trimIndent()

        mockWebServer.enqueue(
            MockResponse()
                .setBody(mockResponse)
                .addHeader("Content-Type", "application/json")
        )

        val result = adapter.search("존재하지않는책")

        assertEquals(0, result.size)
    }
}
