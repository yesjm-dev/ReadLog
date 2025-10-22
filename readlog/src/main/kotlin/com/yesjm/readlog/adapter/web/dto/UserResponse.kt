package com.yesjm.readlog.adapter.web.dto

data class UserResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileImage: String?
)