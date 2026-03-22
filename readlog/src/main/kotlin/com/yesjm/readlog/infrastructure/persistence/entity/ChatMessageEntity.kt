package com.yesjm.readlog.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
class ChatMessageEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val chatRoomId: Long,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: MessageRoleEntity,

    @Column(nullable = false, length = 5000)
    val content: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class MessageRoleEntity {
    USER,
    ASSISTANT
}
