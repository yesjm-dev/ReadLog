package com.yesjm.readlog.domain.exception

sealed class DomainException(message: String) : RuntimeException(message)

class InvalidRatingException(message: String) : DomainException(message)
