package com.yesjm.readlog.infrastructure.external.naver

import com.yesjm.readlog.application.exception.ExternalServiceException
import com.yesjm.readlog.application.port.output.BookSearchPort
import com.yesjm.readlog.domain.model.Book
import com.yesjm.readlog.infrastructure.external.naver.config.NaverApiProperties
import com.yesjm.readlog.infrastructure.external.naver.dto.NaverBookSearchResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import reactor.core.publisher.Mono

@Component
class NaverBookSearchAdapter(
    private val naverApiProperties: NaverApiProperties,
    private val webClient: WebClient
): BookSearchPort {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun search(query: String): List<Book> {
        return try {
            logger.info("네이버 API 책 검색 시작: query=$query")

            val response = webClient.get()
                .uri { uriBuilder ->
                    uriBuilder
                        .path("/v1/search/book.json")
                        .queryParam("query", query)
                        .queryParam("display", 20)
                        .queryParam("start", 1)
                        .build()
                }
                .header("X-Naver-Client-Id", naverApiProperties.clientId)
                .header("X-Naver-Client-Secret", naverApiProperties.clientSecret)
                .retrieve()
                .bodyToMono(NaverBookSearchResponse::class.java)
                .onErrorResume { error ->
                    logger.error("네이버 API 호출 실패", error)
                    Mono.error(ExternalServiceException("네이버 책 검색 API", error))
                }
                .block() ?: throw ExternalServiceException("네이버 책 검색 API", null)

            logger.info("네이버 API 책 검색 완료: 총 ${response.items.size}개")

            response.items.map { item ->
                Book(
                    id = null,
                    title = item.cleanTitle(),
                    author = item.cleanAuthor(),
                    isbn = item.getIsbn13(),
                    imageUrl = item.image.ifBlank { null },
                    publisher = item.publisher.ifBlank { null },
                    description = item.cleanDescription().ifBlank { null }
                )
            }

        } catch (e: WebClientResponseException) {
            logger.error("네이버 API 응답 오류: ${e.statusCode} - ${e.responseBodyAsString}", e)
            throw ExternalServiceException("네이버 책 검색 API", e)
        } catch (e: Exception) {
            logger.error("네이버 API 예상치 못한 오류", e)
            throw ExternalServiceException("네이버 책 검색 API", e)
        }
    }

}