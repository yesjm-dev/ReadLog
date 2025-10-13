package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.Book

interface BookRepository {
    fun save(book: Book): Book
    fun findById(id: Long): Book?
    fun findByIsbn(isbn: String): Book?
}