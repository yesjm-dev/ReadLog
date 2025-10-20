package com.yesjm.readlog.infrastructure.external.naver.dto

data class NaverBookSearchResponse(
    val lastBuildDate: String,
    val total: Int,
    val start: Int,
    val display: Int,
    val items: List<NaverBookItem>
)

data class NaverBookItem(
    val title: String,
    val link: String,
    val image: String,
    val author: String,
    val discount: String,
    val publisher: String,
    val pubdate: String,
    val isbn: String,
    val description: String
) {
    // HTML 태그 제거 (네이버 API는 <b> 태그 포함)
    fun cleanTitle(): String = title.replace("<b>", "").replace("</b>", "")
    fun cleanAuthor(): String = author.replace("<b>", "").replace("</b>", "")
    fun cleanDescription(): String = description.replace("<b>", "").replace("</b>", "")

    // ISBN13 추출 (네이버는 ISBN10과 ISBN13을 공백으로 구분)
    fun getIsbn13(): String? {
        val isbns = isbn.split(" ")
        return isbns.find { it.length == 13 } ?: isbns.firstOrNull()
    }
}
