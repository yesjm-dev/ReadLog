package com.yesjm.readlog.application.port.output

import com.yesjm.readlog.domain.model.User

interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findByEmail(email: String): User?
    fun findByProviderAndProviderId(provider: String, providerId: String): User?
}
