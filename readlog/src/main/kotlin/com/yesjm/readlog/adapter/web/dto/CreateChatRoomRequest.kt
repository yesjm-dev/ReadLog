package com.yesjm.readlog.adapter.web.dto

import jakarta.validation.constraints.NotNull

data class CreateChatRoomRequest(
    @field:NotNull(message = "bookId는 필수입니다")
    val bookId: Long
)
