package com.yesjm.readlog.infrastructure.external.config

import com.yesjm.readlog.infrastructure.external.naver.config.NaverApiProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(NaverApiProperties::class)
class WebClientConfig {
    @Bean
    fun webClient(naverApiProperties: NaverApiProperties): WebClient {
        return WebClient.builder()
            .baseUrl("https://openapi.naver.com")
            .defaultHeader("X-Naver-Client-Id", naverApiProperties.clientId)
            .defaultHeader("X-Naver-Client-Secret", naverApiProperties.clientSecret)
            .build()
    }
}