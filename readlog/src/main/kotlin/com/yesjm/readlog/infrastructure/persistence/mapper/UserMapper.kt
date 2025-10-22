package com.yesjm.readlog.infrastructure.persistence.mapper

import com.yesjm.readlog.domain.model.User
import com.yesjm.readlog.infrastructure.persistence.entity.UserEntity

object UserMapper {
    fun toDomain(entity: UserEntity): User {
        return User(
            id = entity.id,
            email = entity.email,
            name = entity.name,
            profileImage = entity.profileImage,
            provider = entity.provider,
            providerId = entity.providerId,
            createdAt = entity.createdAt
        )
    }

    fun toEntity(domain: User): UserEntity {
        return UserEntity(
            id = domain.id,
            email = domain.email,
            name = domain.name,
            profileImage = domain.profileImage,
            provider = domain.provider,
            providerId = domain.providerId,
            createdAt = domain.createdAt
        )
    }
}