package com.yesjm.readlog.infrastructure.persistence.mapper

import com.yesjm.readlog.domain.model.Book
import com.yesjm.readlog.infrastructure.persistence.entity.BookEntity

object BookMapper {
    fun toDomain(entity: BookEntity): Book {
        return Book(
            id = entity.id,
            title = entity.title,
            author = entity.author,
            isbn = entity.isbn,
            imageUrl = entity.imageUrl,
            publisher = entity.publisher,
            description = entity.description
        )
    }

    fun toEntity(domain: Book): BookEntity {
        return BookEntity(
            id = domain.id,
            title = domain.title,
            author = domain.author,
            isbn = domain.isbn,
            imageUrl = domain.imageUrl,
            publisher = domain.publisher,
            description = domain.description
        )
    }

    fun updateEntity(entity: BookEntity, domain: Book) {
        entity.title = domain.title
        entity.author = domain.author
        entity.isbn = domain.isbn
        entity.imageUrl = domain.imageUrl
        entity.publisher = domain.publisher
        entity.description = domain.description
    }
}