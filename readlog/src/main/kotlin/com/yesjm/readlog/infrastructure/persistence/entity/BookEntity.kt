package com.yesjm.readlog.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "books")
class BookEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    var title: String,

    var author: String? = null,

    @Column(unique = true)
    var isbn: String? = null,

    @Column(length = 1000)
    var imageUrl: String? = null,

    var publisher: String? = null,

    @Column(length = 2000)
    var description: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)