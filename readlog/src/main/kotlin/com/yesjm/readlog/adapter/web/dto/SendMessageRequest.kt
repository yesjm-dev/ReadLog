package com.yesjm.readlog.adapter.web.dto

import jakarta.validation.constraints.NotBlank

data class SendMessageRequest(
    @field:NotBlank(message = "메시지 내용은 비어있을 수 없습니다")
    val content: String
)
