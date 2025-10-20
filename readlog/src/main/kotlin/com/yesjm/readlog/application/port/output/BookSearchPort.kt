package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.Book

interface BookSearchPort {
    fun search(query: String): List<Book>
}