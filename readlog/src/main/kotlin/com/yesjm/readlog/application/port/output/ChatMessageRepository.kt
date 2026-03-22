package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.ChatMessage

interface ChatMessageRepository {
    fun save(chatMessage: ChatMessage): ChatMessage
    fun findByChatRoomId(chatRoomId: Long): List<ChatMessage>
    fun countByChatRoomId(chatRoomId: Long): Long
    fun findRecentByChatRoomId(chatRoomId: Long, limit: Int): List<ChatMessage>
    fun deleteByChatRoomId(chatRoomId: Long)
}
