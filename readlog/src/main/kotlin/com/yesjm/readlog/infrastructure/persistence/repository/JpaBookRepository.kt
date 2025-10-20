package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.BookEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaBookRepository: JpaRepository<BookEntity, Long> {
    fun findByIsbn(isbn: String): BookEntity?
    fun findByTitleContaining(title: String): List<BookEntity>
}