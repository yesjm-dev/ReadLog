package com.yesjm.readlog.infrastructure.persistence.repository

import com.yesjm.readlog.infrastructure.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface JpaUserRepository : JpaRepository<UserEntity, Long> {
    fun findByEmail(email: String): UserEntity?
    fun findByProviderAndProviderId(provider: String, providerId: String): UserEntity?
}