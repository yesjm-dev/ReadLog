package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.ChatRoom

interface ChatRoomRepository {
    fun save(chatRoom: ChatRoom): ChatRoom
    fun findById(id: Long): ChatRoom?
    fun findByUserIdAndBookId(userId: Long, bookId: Long): ChatRoom?
    fun findByUserId(userId: Long): List<ChatRoom>
    fun delete(id: Long)
}
