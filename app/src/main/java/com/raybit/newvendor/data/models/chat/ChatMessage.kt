package com.raybit.newvendor.data.models.chat

data class ChatMessage(
    val consultation_request_id: Int,
    val created_at: String,
    val file_url: String,
    val id: Int,
    val message: String,
    val receiver_id: Int,
    val sender_id: Int,
    val sentAt: Long,
    val type: String,
    val status: String,
    val updated_at: String
)