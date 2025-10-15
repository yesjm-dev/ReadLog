package com.yesjm.readlog.application.exception

sealed class ApplicationException(message: String) : RuntimeException(message)

class BookNotFoundException(bookId: Long) :
    ApplicationException("책을 찾을 수 없습니다: $bookId")

class ReadingRecordNotFoundException(recordId: Long) :
    ApplicationException("독서 기록을 찾을 수 없습니다: $recordId")