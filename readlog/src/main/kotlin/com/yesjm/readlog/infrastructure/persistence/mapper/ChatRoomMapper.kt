package com.yesjm.readlog.infrastructure.persistence.mapper

import com.yesjm.readlog.domain.model.ChatRoom
import com.yesjm.readlog.infrastructure.persistence.entity.ChatRoomEntity

object ChatRoomMapper {
    fun toDomain(entity: ChatRoomEntity): ChatRoom {
        return ChatRoom(
            id = entity.id,
            userId = entity.userId,
            bookId = entity.bookId,
            summary = entity.summary,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toEntity(domain: ChatRoom): ChatRoomEntity {
        return ChatRoomEntity(
            id = domain.id,
            userId = domain.userId,
            bookId = domain.bookId,
            summary = domain.summary,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }
}
