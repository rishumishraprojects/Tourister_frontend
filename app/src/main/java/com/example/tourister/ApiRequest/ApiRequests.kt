package com.example.tourister.ApiRequest

import java.util.Optional

data class ChatMessageHistory(
    val role: String,
    val text: String
)

data class ConversationRequest(
    val history: List<ChatMessageHistory>,
    val prompt: String,
)

data class TripSuggestion(
    val location: String? = null,
    val description: String? = null,
    val budget: String? = null,
    val error: String? = null,
    val picture_url: String? = null
)

data class PaymentRequest(
    val amount: Int,
    val receipt_id: String
)

data class OrderResponse(
    val order_id: String,
    val amount: Int,
    val currency: String
)