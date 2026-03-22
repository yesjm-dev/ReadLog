package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.ChatRoomEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaChatRoomRepository : JpaRepository<ChatRoomEntity, Long> {
    fun findByUserIdAndBookId(userId: Long, bookId: Long): ChatRoomEntity?
    fun findByUserIdOrderByUpdatedAtDesc(userId: Long): List<ChatRoomEntity>
}
