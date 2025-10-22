package com.yesjm.readlog.domain.model

import java.time.LocalDateTime

data class User (
    val id: Long?,
    val email: String,
    val name: String,
    val profileImage: String?,
    val provider: String, // "naver"
    val providerId: String, // 네이버 고유 ID
    val createdAt: LocalDateTime = LocalDateTime.now()
)