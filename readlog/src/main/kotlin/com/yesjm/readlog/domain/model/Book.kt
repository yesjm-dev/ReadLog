package com.yesjm.readlog.domain.model

data class Book(
    val id: Long?,
    val title: String,
    val author: String?,
    val isbn: String?,
    val imageUrl: String? = null,
    val publisher: String? = null,
    val description: String? = null
) {
    init {
        require(title.isNotBlank()) { "책 제목은 비어있을 수 없습니다" }
    }
}