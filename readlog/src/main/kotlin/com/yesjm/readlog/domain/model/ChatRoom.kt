package com.yesjm.readlog.domain.model

import java.time.LocalDateTime

data class ChatRoom(
    val id: Long?,
    val userId: Long,
    val bookId: Long,
    val summary: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val updatedAt: LocalDateTime = LocalDateTime.now()
)
