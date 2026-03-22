package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.application.port.output.ChatRoomRepository
import com.yesjm.readlog.domain.model.ChatRoom
import com.yesjm.readlog.infrastructure.persistence.mapper.ChatRoomMapper
import com.yesjm.readlog.infrastructure.persistence.repository.JpaChatRoomRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ChatRoomRepositoryAdapter(
    private val jpaChatRoomRepository: JpaChatRoomRepository
) : ChatRoomRepository {

    override fun save(chatRoom: ChatRoom): ChatRoom {
        val entity = if (chatRoom.id != null) {
            jpaChatRoomRepository.findByIdOrNull(chatRoom.id)?.also {
                it.summary = chatRoom.summary
                it.updatedAt = chatRoom.updatedAt
            } ?: ChatRoomMapper.toEntity(chatRoom)
        } else {
            ChatRoomMapper.toEntity(chatRoom)
        }
        val saved = jpaChatRoomRepository.save(entity)
        return ChatRoomMapper.toDomain(saved)
    }

    override fun findById(id: Long): ChatRoom? {
        return jpaChatRoomRepository.findByIdOrNull(id)?.let {
            ChatRoomMapper.toDomain(it)
        }
    }

    override fun findByUserIdAndBookId(userId: Long, bookId: Long): ChatRoom? {
        return jpaChatRoomRepository.findByUserIdAndBookId(userId, bookId)?.let {
            ChatRoomMapper.toDomain(it)
        }
    }

    override fun findByUserId(userId: Long): List<ChatRoom> {
        return jpaChatRoomRepository.findByUserIdOrderByUpdatedAtDesc(userId).map {
            ChatRoomMapper.toDomain(it)
        }
    }

    override fun delete(id: Long) {
        jpaChatRoomRepository.deleteById(id)
    }
}
