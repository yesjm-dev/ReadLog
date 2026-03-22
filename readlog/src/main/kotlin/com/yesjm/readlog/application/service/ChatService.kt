package com.yesjm.readlog.application.service

import com.yesjm.readlog.application.exception.BookNotFoundException
import com.yesjm.readlog.application.port.input.ChatUseCase
import com.yesjm.readlog.application.port.output.*
import com.yesjm.readlog.application.service.dto.ChatMessageResponse
import com.yesjm.readlog.application.service.dto.ChatRoomResponse
import com.yesjm.readlog.domain.model.ChatMessage
import com.yesjm.readlog.domain.model.ChatRoom
import com.yesjm.readlog.domain.model.MessageRole
import com.yesjm.readlog.domain.model.ReadingRecord
import com.yesjm.readlog.domain.model.ReadingStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ChatService(
    private val chatRoomRepository: ChatRoomRepository,
    private val chatMessageRepository: ChatMessageRepository,
    private val readingRecordRepository: ReadingRecordRepository,
    private val bookRepository: BookRepository,
    private val aiChatPort: AiChatPort
) : ChatUseCase {

    companion object {
        private const val MAX_CONTEXT_MESSAGES = 20
    }

    @Transactional
    override fun getOrCreateChatRoom(userId: Long, bookId: Long): ChatRoomResponse {
        val existing = chatRoomRepository.findByUserIdAndBookId(userId, bookId)
        if (existing != null) {
            val book = bookRepository.findById(bookId) ?: throw BookNotFoundException(bookId)
            val messages = chatMessageRepository.findByChatRoomId(existing.id!!)
            return ChatRoomResponse(
                id = existing.id,
                userId = existing.userId,
                bookId = existing.bookId,
                bookTitle = book.title,
                bookImageUrl = book.imageUrl,
                createdAt = existing.createdAt,
                updatedAt = existing.updatedAt,
                messages = messages.map { ChatMessageResponse.from(it) }
            )
        }

        val book = bookRepository.findById(bookId) ?: throw BookNotFoundException(bookId)
        val chatRoom = chatRoomRepository.save(
            ChatRoom(id = null, userId = userId, bookId = bookId)
        )
        return ChatRoomResponse(
            id = chatRoom.id!!,
            userId = chatRoom.userId,
            bookId = chatRoom.bookId,
            bookTitle = book.title,
            bookImageUrl = book.imageUrl,
            createdAt = chatRoom.createdAt,
            updatedAt = chatRoom.updatedAt
        )
    }

    override fun getChatRooms(userId: Long): List<ChatRoomResponse> {
        return chatRoomRepository.findByUserId(userId).map { chatRoom ->
            val book = bookRepository.findById(chatRoom.bookId)
            ChatRoomResponse(
                id = chatRoom.id!!,
                userId = chatRoom.userId,
                bookId = chatRoom.bookId,
                bookTitle = book?.title ?: "알 수 없는 책",
                bookImageUrl = book?.imageUrl,
                createdAt = chatRoom.createdAt,
                updatedAt = chatRoom.updatedAt
            )
        }
    }

    override fun getChatRoom(id: Long, userId: Long): ChatRoomResponse {
        val chatRoom = chatRoomRepository.findById(id)
            ?: throw IllegalArgumentException("채팅방을 찾을 수 없습니다: $id")
        val book = bookRepository.findById(chatRoom.bookId)
        val messages = chatMessageRepository.findByChatRoomId(id)
        return ChatRoomResponse(
            id = chatRoom.id!!,
            userId = chatRoom.userId,
            bookId = chatRoom.bookId,
            bookTitle = book?.title ?: "알 수 없는 책",
            bookImageUrl = book?.imageUrl,
            createdAt = chatRoom.createdAt,
            updatedAt = chatRoom.updatedAt,
            messages = messages.map { ChatMessageResponse.from(it) }
        )
    }

    @Transactional
    override fun sendMessage(chatRoomId: Long, userId: Long, content: String): ChatMessageResponse {
        val chatRoom = chatRoomRepository.findById(chatRoomId)
            ?: throw IllegalArgumentException("채팅방을 찾을 수 없습니다: $chatRoomId")

        // 1. 사용자 메시지 저장
        val userMessage = chatMessageRepository.save(
            ChatMessage(id = null, chatRoomId = chatRoomId, role = MessageRole.USER, content = content)
        )

        // 2. 컨텍스트 구성
        val book = bookRepository.findById(chatRoom.bookId)
            ?: throw BookNotFoundException(chatRoom.bookId)
        val readingRecords = readingRecordRepository.findByUserId(userId)
        val systemPrompt = buildSystemPrompt(book, readingRecords)

        // 3. 토큰 관리: 메시지 수 확인 후 요약 처리
        val messageCount = chatMessageRepository.countByChatRoomId(chatRoomId)
        var updatedChatRoom = chatRoom
        if (messageCount > MAX_CONTEXT_MESSAGES) {
            updatedChatRoom = summarizeOldMessages(chatRoom)
        }

        // 4. 컨텍스트 메시지 구성
        val contextMessages = buildContextMessages(updatedChatRoom, chatRoomId)

        // 5. AI 호출
        val aiResponse = aiChatPort.chat(systemPrompt, contextMessages)

        // 6. AI 응답 저장
        val assistantMessage = chatMessageRepository.save(
            ChatMessage(id = null, chatRoomId = chatRoomId, role = MessageRole.ASSISTANT, content = aiResponse)
        )

        // 7. 채팅방 updatedAt 갱신
        chatRoomRepository.save(updatedChatRoom.copy(updatedAt = LocalDateTime.now()))

        return ChatMessageResponse.from(assistantMessage)
    }

    @Transactional
    override fun deleteChatRoom(id: Long, userId: Long) {
        val chatRoom = chatRoomRepository.findById(id)
            ?: throw IllegalArgumentException("채팅방을 찾을 수 없습니다: $id")
        chatMessageRepository.deleteByChatRoomId(id)
        chatRoomRepository.delete(id)
    }

    private fun buildSystemPrompt(book: com.yesjm.readlog.domain.model.Book, readingRecords: List<ReadingRecord>): String {
        val recordsSummary = if (readingRecords.isEmpty()) {
            "사용자의 독서 기록이 아직 없습니다."
        } else {
            readingRecords.joinToString("\n") { record ->
                val rating = "평점: ${record.rating.value}/5"
                val review = record.review?.let { ", 리뷰: \"$it\"" } ?: ""
                val status = when (record.status) {
                    ReadingStatus.READING -> "읽는 중"
                    ReadingStatus.COMPLETED -> "완독"
                    ReadingStatus.DROPPED -> "중단"
                }
                "- \"${record.book.title}\" (${record.book.author ?: "저자 미상"}) / $status / $rating$review"
            }
        }

        return """
            너는 독서 추천 도우미야. 사용자의 독서 기록을 바탕으로 새 책이 사용자에게 맞을지 분석해줘.
            친근하고 자연스러운 한국어로 대화해줘.

            [사용자 독서 기록]
            $recordsSummary

            [분석 대상 책 정보]
            - 제목: ${book.title}
            - 저자: ${book.author ?: "미상"}
            - 출판사: ${book.publisher ?: "미상"}
            - 설명: ${book.description ?: "없음"}
        """.trimIndent()
    }

    private fun summarizeOldMessages(chatRoom: ChatRoom): ChatRoom {
        val allMessages = chatMessageRepository.findByChatRoomId(chatRoom.id!!)
        val oldMessages = allMessages.dropLast(MAX_CONTEXT_MESSAGES)

        if (oldMessages.isEmpty()) return chatRoom

        val messagePairs = oldMessages.map { it.role.name to it.content }
        val existingSummary = chatRoom.summary?.let { "이전 요약: $it\n\n" } ?: ""
        val summaryPrompt = existingSummary + "위 대화를 포함하여 다음 대화를 간결하게 요약해줘."

        val newSummary = aiChatPort.summarize(
            listOf("SYSTEM" to summaryPrompt) + messagePairs
        )

        return chatRoomRepository.save(chatRoom.copy(summary = newSummary))
    }

    private fun buildContextMessages(chatRoom: ChatRoom, chatRoomId: Long): List<Pair<String, String>> {
        val messages = mutableListOf<Pair<String, String>>()

        chatRoom.summary?.let {
            messages.add("SYSTEM" to "[이전 대화 요약]\n$it")
        }

        val recentMessages = chatMessageRepository.findRecentByChatRoomId(chatRoomId, MAX_CONTEXT_MESSAGES)
        recentMessages.forEach {
            messages.add(it.role.name to it.content)
        }

        return messages
    }
}
