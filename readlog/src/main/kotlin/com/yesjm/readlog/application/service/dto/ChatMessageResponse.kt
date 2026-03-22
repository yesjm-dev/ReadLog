package com.yesjm.readlog.application.service.dto

import com.yesjm.readlog.domain.model.ChatMessage
import com.yesjm.readlog.domain.model.MessageRole
import java.time.LocalDateTime

data class ChatMessageResponse(
    val id: Long,
    val chatRoomId: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(chatMessage: ChatMessage): ChatMessageResponse {
            return ChatMessageResponse(
                id = chatMessage.id!!,
                chatRoomId = chatMessage.chatRoomId,
                role = chatMessage.role,
                content = chatMessage.content,
                createdAt = chatMessage.createdAt
            )
        }
    }
}
