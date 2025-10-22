package com.yesjm.readlog.infrastructure.persistence.adapter

import com.yesjm.readlog.application.port.output.UserRepository
import com.yesjm.readlog.domain.model.User
import com.yesjm.readlog.infrastructure.persistence.mapper.UserMapper
import com.yesjm.readlog.infrastructure.persistence.repository.JpaUserRepository
import org.springframework.stereotype.Repository

@Repository
class UserRepositoryAdapter(
    private val jpaUserRepository: JpaUserRepository
) : UserRepository {
    override fun save(user: User): User {
        val entity = UserMapper.toEntity(user)
        val saved = jpaUserRepository.save(entity)
        return UserMapper.toDomain(saved)
    }

    override fun findById(id: Long): User? {
        return jpaUserRepository.findById(id).orElse(null)?.let { UserMapper.toDomain(it) }
    }

    override fun findByEmail(email: String): User? {
        return jpaUserRepository.findByEmail(email)?.let { UserMapper.toDomain(it) }
    }

    override fun findByProviderAndProviderId(provider: String, providerId: String): User? {
        return jpaUserRepository.findByProviderAndProviderId(provider, providerId)?.let {
            UserMapper.toDomain(it)
        }
    }
}