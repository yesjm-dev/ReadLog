package com.yesjm.readlog.infrastructure.security

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.security.oauth2.client")
data class OAuth2Properties(
    val registration: Map<String, Registration>,
    val provider: Map<String, Provider>
) {
    data class Registration(
        val clientId: String,
        val clientSecret: String,
        val redirectUri: String,
        val authorizationGrantType: String,
        val clientName: String,
        val scope: List<String>
    )

    data class Provider(
        val authorizationUri: String,
        val tokenUri: String,
        val userInfoUri: String,
        val userNameAttribute: String
    )
}