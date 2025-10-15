package com.yesjm.readlog.infrastructure.external.naver.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "naver.api")
data class NaverApiProperties(
    val clientId: String,
    val clientSecret: String,
)