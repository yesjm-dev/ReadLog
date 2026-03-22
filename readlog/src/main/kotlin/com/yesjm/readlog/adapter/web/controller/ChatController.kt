package com.yesjm.readlog.adapter.web.controller

import com.yesjm.readlog.adapter.web.dto.CreateChatRoomRequest
import com.yesjm.readlog.adapter.web.dto.SendMessageRequest
import com.yesjm.readlog.application.port.input.ChatUseCase
import com.yesjm.readlog.application.service.dto.ChatMessageResponse
import com.yesjm.readlog.application.service.dto.ChatRoomResponse
import com.yesjm.readlog.infrastructure.security.JwtAuthentication
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/chat-rooms")
class ChatController(
    private val chatUseCase: ChatUseCase
) {

    @PostMapping
    fun createChatRoom(
        @Valid @RequestBody request: CreateChatRoomRequest,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ChatRoomResponse> {
        val response = chatUseCase.getOrCreateChatRoom(jwtAuth.userId, request.bookId)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping
    fun getChatRooms(
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<List<ChatRoomResponse>> {
        val response = chatUseCase.getChatRooms(jwtAuth.userId)
        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    fun getChatRoom(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ChatRoomResponse> {
        val response = chatUseCase.getChatRoom(id, jwtAuth.userId)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/{id}/messages")
    fun sendMessage(
        @PathVariable id: Long,
        @Valid @RequestBody request: SendMessageRequest,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<ChatMessageResponse> {
        val response = chatUseCase.sendMessage(id, jwtAuth.userId, request.content)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @DeleteMapping("/{id}")
    fun deleteChatRoom(
        @PathVariable id: Long,
        @AuthenticationPrincipal jwtAuth: JwtAuthentication
    ): ResponseEntity<Void> {
        chatUseCase.deleteChatRoom(id, jwtAuth.userId)
        return ResponseEntity.noContent().build()
    }
}
