package com.yesjm.readlog.domain.model

import com.yesjm.readlog.domain.exception.InvalidRatingException

class Rating(val value: Int) {
    init {
        if (value !in 1..5) {
            throw InvalidRatingException("평점은 1에서 5 사이여야 합니다. 현재: $value")
        }
    }
}