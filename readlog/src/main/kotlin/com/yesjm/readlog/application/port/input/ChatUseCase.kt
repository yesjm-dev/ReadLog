package com.yesjm.readlog.application.port.input

import com.yesjm.readlog.application.service.dto.ChatMessageResponse
import com.yesjm.readlog.application.service.dto.ChatRoomResponse

interface ChatUseCase {
    fun getOrCreateChatRoom(userId: Long, bookId: Long): ChatRoomResponse
    fun getChatRooms(userId: Long): List<ChatRoomResponse>
    fun getChatRoom(id: Long, userId: Long): ChatRoomResponse
    fun sendMessage(chatRoomId: Long, userId: Long, content: String): ChatMessageResponse
    fun deleteChatRoom(id: Long, userId: Long)
}
