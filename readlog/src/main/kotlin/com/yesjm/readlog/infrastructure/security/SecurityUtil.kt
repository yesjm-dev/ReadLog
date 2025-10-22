package com.yesjm.readlog.infrastructure.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil {

    fun getCurrentUserId(): Long {
        val authentication = SecurityContextHolder.getContext().authentication
        return authentication?.principal as? Long
            ?: throw IllegalStateException("인증되지 않은 사용자입니다")
    }
}
