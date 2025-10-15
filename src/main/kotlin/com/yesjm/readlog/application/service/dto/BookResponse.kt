package com.yesjm.readlog.application.service.dto

import com.yesjm.readlog.domain.model.Book

data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String?,
    val imageUrl: String?,
    val publisher: String?,
    val description: String?
) {
    companion object {
        fun from(book: Book): BookResponse {
            return BookResponse(
                id = book.id!!,
                title = book.title,
                author = book.author,
                isbn = book.isbn,
                imageUrl = book.imageUrl,
                publisher = book.publisher,
                description = book.description
            )
        }
    }
}
