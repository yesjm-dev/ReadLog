package com.yesjm.readlog.infrastructure.external.gemini

import com.yesjm.readlog.application.port.output.AiChatPort
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.messages.AssistantMessage
import org.springframework.ai.chat.messages.Message
import org.springframework.ai.chat.messages.SystemMessage
import org.springframework.ai.chat.messages.UserMessage
import org.springframework.stereotype.Component

@Component
class GeminiChatAdapter(
    private val chatClient: ChatClient
) : AiChatPort {

    override fun chat(systemPrompt: String, messages: List<Pair<String, String>>): String {
        val chatMessages = mutableListOf<Message>()
        chatMessages.add(SystemMessage(systemPrompt))

        messages.forEach { (role, content) ->
            when (role) {
                "USER" -> chatMessages.add(UserMessage(content))
                "ASSISTANT" -> chatMessages.add(AssistantMessage(content))
                "SYSTEM" -> chatMessages.add(SystemMessage(content))
            }
        }

        return chatClient.prompt()
            .messages(chatMessages)
            .call()
            .content() ?: "응답을 생성할 수 없습니다."
    }

    override fun summarize(messages: List<Pair<String, String>>): String {
        val chatMessages = mutableListOf<Message>()
        chatMessages.add(SystemMessage("다음 대화 내용을 간결하게 요약해줘. 핵심 내용과 사용자의 관심사를 중심으로 정리해줘."))

        messages.forEach { (role, content) ->
            when (role) {
                "USER" -> chatMessages.add(UserMessage(content))
                "ASSISTANT" -> chatMessages.add(AssistantMessage(content))
                "SYSTEM" -> chatMessages.add(SystemMessage(content))
            }
        }

        chatMessages.add(UserMessage("위 대화를 간결하게 요약해줘."))

        return chatClient.prompt()
            .messages(chatMessages)
            .call()
            .content() ?: ""
    }
}
