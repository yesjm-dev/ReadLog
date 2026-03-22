package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.ChatMessageEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaChatMessageRepository : JpaRepository<ChatMessageEntity, Long> {
    fun findByChatRoomIdOrderByCreatedAtAsc(chatRoomId: Long): List<ChatMessageEntity>
    fun countByChatRoomId(chatRoomId: Long): Long
    fun deleteByChatRoomId(chatRoomId: Long)
}
