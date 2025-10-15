package com.yesjm.readlog.application.port.input

import com.yesjm.readlog.adapter.web.dto.CreateBookRequest
import com.yesjm.readlog.application.service.dto.BookResponse

interface SearchBookUseCase {
    fun search(query: String): List<BookResponse>
    fun createBook(request: CreateBookRequest): BookResponse
    fun getById(id: Long): BookResponse
}