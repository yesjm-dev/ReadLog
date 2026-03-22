package com.yesjm.readlog.application.service.dto

import java.time.LocalDateTime

data class ChatRoomResponse(
    val id: Long,
    val userId: Long,
    val bookId: Long,
    val bookTitle: String,
    val bookImageUrl: String?,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val messages: List<ChatMessageResponse> = emptyList()
)
