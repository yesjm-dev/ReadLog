package com.yesjm.readlog.domain.model

import java.time.LocalDateTime

data class ChatMessage(
    val id: Long?,
    val chatRoomId: Long,
    val role: MessageRole,
    val content: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class MessageRole {
    USER,
    ASSISTANT
}
