package com.yesjm.readlog.infrastructure.security

import com.yesjm.readlog.application.port.output.UserRepository
import com.yesjm.readlog.domain.model.User
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import java.net.URLEncoder

@Component
class OAuth2SuccessHandler(
    private val jwtUtil: JwtUtil,
    private val userRepository: UserRepository
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val oAuth2User = authentication.principal as OAuth2User

        // 네이버 사용자 정보 추출
        val attributes = oAuth2User.getAttribute<Map<String, Any>>("response")!!
        val providerId = attributes["id"] as String
        val email = attributes["email"] as String
        val name = attributes["name"] as String
        val profileImage = attributes["profile_image"] as? String

        // 기존 사용자 찾기 또는 새로 생성
        var user = userRepository.findByProviderAndProviderId("naver", providerId)
        if (user == null) {
            user = userRepository.save(
                User(
                    id = null,
                    email = email,
                    name = name,
                    profileImage = profileImage,
                    provider = "naver",
                    providerId = providerId
                )
            )
        }

        // JWT 토큰 생성
        val token = jwtUtil.generateToken(user.id!!, user.email)

        // 프론트엔드로 리다이렉트 (토큰 전달)
        val targetUrl = "http://localhost:5173/auth/callback?token=${URLEncoder.encode(token, "UTF-8")}"
        redirectStrategy.sendRedirect(request, response, targetUrl)
    }
}
