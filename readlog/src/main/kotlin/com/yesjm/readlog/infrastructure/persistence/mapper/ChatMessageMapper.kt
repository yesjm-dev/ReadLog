package com.yesjm.readlog.infrastructure.persistence.mapper

import com.yesjm.readlog.domain.model.ChatMessage
import com.yesjm.readlog.domain.model.MessageRole
import com.yesjm.readlog.infrastructure.persistence.entity.ChatMessageEntity
import com.yesjm.readlog.infrastructure.persistence.entity.MessageRoleEntity

object ChatMessageMapper {
    fun toDomain(entity: ChatMessageEntity): ChatMessage {
        return ChatMessage(
            id = entity.id,
            chatRoomId = entity.chatRoomId,
            role = when (entity.role) {
                MessageRoleEntity.USER -> MessageRole.USER
                MessageRoleEntity.ASSISTANT -> MessageRole.ASSISTANT
            },
            content = entity.content,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: ChatMessage): ChatMessageEntity {
        return ChatMessageEntity(
            id = domain.id,
            chatRoomId = domain.chatRoomId,
            role = when (domain.role) {
                MessageRole.USER -> MessageRoleEntity.USER
                MessageRole.ASSISTANT -> MessageRoleEntity.ASSISTANT
            },
            content = domain.content,
            createdAt = domain.createdAt
        )
    }
}
