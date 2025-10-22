package com.yesjm.readlog.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
class UserEntity (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    var email: String,

    @Column(nullable = false)
    var name: String,

    var profileImage: String? = null,

    @Column(nullable = false)
    val provider: String, // "naver"

    @Column(nullable = false, unique = true)
    val providerId: String, // provider에서의 고유 ID

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)