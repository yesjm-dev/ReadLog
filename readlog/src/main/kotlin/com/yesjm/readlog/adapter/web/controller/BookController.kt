package com.yesjm.readlog.adapter.web.controller

import com.yesjm.readlog.adapter.web.dto.CreateBookRequest
import com.yesjm.readlog.application.port.input.SearchBookUseCase
import com.yesjm.readlog.application.service.dto.BookResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(
    private val searchBookUseCase: SearchBookUseCase,
) {

    @PostMapping
    fun createBook(
        @Valid @RequestBody request: CreateBookRequest
    ): ResponseEntity<BookResponse> {
        val response = searchBookUseCase.createBook(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Long): ResponseEntity<BookResponse> {
        val response = searchBookUseCase.getById(id)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/search")
    fun searchBooks(@RequestParam query: String): ResponseEntity<List<BookResponse>> {
        val books = searchBookUseCase.search(query)
        return ResponseEntity.ok(books)
    }
}