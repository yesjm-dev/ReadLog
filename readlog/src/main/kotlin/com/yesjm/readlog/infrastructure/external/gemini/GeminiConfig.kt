package com.yesjm.readlog.infrastructure.external.gemini

import org.springframework.ai.chat.client.ChatClient
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GeminiConfig {

    @Bean
    fun chatClient(builder: ChatClient.Builder): ChatClient {
        return builder.build()
    }
}
