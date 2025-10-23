package com.yesjm.readlog.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

@Entity
@Table(name = "reading_records")
class ReadingRecordEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    var book: BookEntity,

    @Column(nullable = false)
    var rating: Int,

    var startDate: LocalDate? = null,

    var endDate: LocalDate? = null,

    @Column(length = 2000)
    var review: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: ReadingStatusEntity,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)

enum class ReadingStatusEntity {
    READING,
    COMPLETED,
    DROPPED
}
