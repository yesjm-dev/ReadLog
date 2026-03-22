package com.yesjm.readlog.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(
    name = "chat_rooms",
    uniqueConstraints = [UniqueConstraint(columnNames = ["userId", "bookId"])]
)
class ChatRoomEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @Column(nullable = false)
    val bookId: Long,

    @Column(length = 5000)
    var summary: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
