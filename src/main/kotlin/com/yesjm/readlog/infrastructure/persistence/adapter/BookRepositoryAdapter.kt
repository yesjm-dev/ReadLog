package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.application.port.output.BookRepository
import com.yesjm.readlog.domain.model.Book
import com.yesjm.readlog.infrastructure.persistence.mapper.BookMapper
import com.yesjm.readlog.infrastructure.persistence.repository.JpaBookRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class BookRepositoryAdapter(
    private val jpaBookRepository: JpaBookRepository
): BookRepository {
    override fun save(book: Book): Book {
        val entity = if (book.id != null) {
            jpaBookRepository.findByIdOrNull(book.id)?.also {
                BookMapper.updateEntity(it, book)
            } ?: BookMapper.toEntity(book)
        } else {
            BookMapper.toEntity(book)
        }

        val saved = jpaBookRepository.save(entity)
        return BookMapper.toDomain(saved)
    }

    override fun findById(id: Long): Book? {
        return jpaBookRepository.findByIdOrNull(id)?.let { BookMapper.toDomain(it) }
    }

    override fun findByIsbn(isbn: String): Book? {
        return jpaBookRepository.findByIsbn(isbn)?.let { BookMapper.toDomain(it) }
    }
}