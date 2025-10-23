package com.yesjm.readlog.adapter.web.controller

import com.yesjm.readlog.adapter.web.dto.UserResponse
import com.yesjm.readlog.application.port.output.UserRepository
import com.yesjm.readlog.infrastructure.security.JwtAuthentication
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/user")
class UserController(
    private val userRepository: UserRepository
) {

    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<UserResponse> {
        val user = userRepository.findById(jwtAuth.userId)
            ?: return ResponseEntity.notFound().build()

        return ResponseEntity.ok(
            UserResponse(
                id = user.id!!,
                email = user.email,
                name = user.name,
                profileImage = user.profileImage
            )
        )
    }
}

