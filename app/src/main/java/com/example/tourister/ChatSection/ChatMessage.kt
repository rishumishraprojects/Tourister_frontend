package com.example.tourister

enum class MessageType {
    USER, AI, TYPING
}

data class ChatMessage(val message: String, val type: MessageType)