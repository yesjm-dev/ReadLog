package com.yesjm.readlog.application.port.output

interface AiChatPort {
    fun chat(systemPrompt: String, messages: List<Pair<String, String>>): String
    fun summarize(messages: List<Pair<String, String>>): String
}
