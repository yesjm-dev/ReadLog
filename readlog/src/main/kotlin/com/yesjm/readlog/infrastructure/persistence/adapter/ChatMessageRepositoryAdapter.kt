package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.application.port.output.ChatMessageRepository
import com.yesjm.readlog.domain.model.ChatMessage
import com.yesjm.readlog.infrastructure.persistence.mapper.ChatMessageMapper
import com.yesjm.readlog.infrastructure.persistence.repository.JpaChatMessageRepository
import org.springframework.stereotype.Repository

@Repository
class ChatMessageRepositoryAdapter(
    private val jpaChatMessageRepository: JpaChatMessageRepository
) : ChatMessageRepository {

    override fun save(chatMessage: ChatMessage): ChatMessage {
        val entity = ChatMessageMapper.toEntity(chatMessage)
        val saved = jpaChatMessageRepository.save(entity)
        return ChatMessageMapper.toDomain(saved)
    }

    override fun findByChatRoomId(chatRoomId: Long): List<ChatMessage> {
        return jpaChatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId).map {
            ChatMessageMapper.toDomain(it)
        }
    }

    override fun countByChatRoomId(chatRoomId: Long): Long {
        return jpaChatMessageRepository.countByChatRoomId(chatRoomId)
    }

    override fun findRecentByChatRoomId(chatRoomId: Long, limit: Int): List<ChatMessage> {
        val all = jpaChatMessageRepository.findByChatRoomIdOrderByCreatedAtAsc(chatRoomId)
        return all.takeLast(limit).map { ChatMessageMapper.toDomain(it) }
    }

    override fun deleteByChatRoomId(chatRoomId: Long) {
        jpaChatMessageRepository.deleteByChatRoomId(chatRoomId)
    }
}
